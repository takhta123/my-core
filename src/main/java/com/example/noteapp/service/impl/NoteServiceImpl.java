package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.entity.Note;
import com.example.noteapp.entity.User;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.NoteService;
import com.example.noteapp.repository.LabelRepository;
import com.example.noteapp.entity.Label;
import com.example.noteapp.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page; // Import mới
import org.springframework.data.domain.PageRequest; // Import mới
import org.springframework.data.domain.Pageable; // Import mới
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final FileStorageService fileStorageService;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 2. Lấy Note theo ID và kiểm tra xem User hiện tại có phải chủ sở hữu không
    private Note getNoteAndCheckOwner(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        User currentUser = getCurrentUser();

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }
        return note;
    }

    @Override
    @Transactional
    public Note createNote(String email, NoteRequest request) {
        User user = getUserByEmail(email);

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .backgroundColor(request.getBackgroundColor())
                .isPinned(request.isPinned())
                .isArchived(request.isArchived())
                .reminder(request.getReminder())
                .isReminderSent(false)
                .isDeleted(false) // Mặc định không nằm trong thùng rác
                .user(user)       // Gán user sở hữu
                .build();

        return noteRepository.save(note);
    }

    @Override
    public Page<Note> getAllNotes(String email, int page, int size) {
        User user = getUserByEmail(email);

        // --- SỬA ĐOẠN NÀY ---
        // Sắp xếp:
        // 1. isPinned giảm dần (Ghim lên đầu)
        // 2. createdAt giảm dần (Mới tạo -> Cũ tạo)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt"))); // <--- Đổi updatedAt thành createdAt
        // --------------------

        return noteRepository.findByUserIdAndIsDeletedFalseAndIsArchivedFalse(user.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Note> getArchivedNotes(int page, int size) {
        User user = getCurrentUser(); // Hàm helper lấy user hiện tại
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return noteRepository.findByUserIdAndIsArchivedTrueAndIsDeletedFalse(user.getId(), pageable);
    }

    // [MỚI] Lấy danh sách Thùng rác
    @Override
    @Transactional(readOnly = true)
    public Page<Note> getTrashedNotes(int page, int size) {
        User user = getCurrentUser();
        // Thùng rác thường sắp xếp theo ngày xóa (updatedAt)
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return noteRepository.findByUserIdAndIsDeletedTrue(user.getId(), pageable);
    }

    @Override
    public List<Note> getArchivedNotes(String email) {
        return List.of();
    }

    @Override
    public List<Note> getTrashedNotes(String email) {
        return List.of();
    }

    @Override
    public List<Note> searchNotes(String email, String keyword) {
        User user = getUserByEmail(email);

        // Gọi hàm searchNotes tùy chỉnh vừa viết trong Repository
        return noteRepository.searchNotes(user.getId(), keyword);
    }

    @Override
    public Note getNoteById(Long noteId, String email) {
        User user = getUserByEmail(email);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Ghi chú không tồn tại"));

        // Kiểm tra quyền sở hữu (Bảo mật)
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền truy cập ghi chú này");
        }
        return note;
    }

    @Override
    @Transactional
    public Note updateNote(Long noteId, String email, NoteRequest request) {
        Note note = getNoteById(noteId, email); // Tận dụng hàm trên để lấy và check quyền

        // Cập nhật các trường
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setBackgroundColor(request.getBackgroundColor());
        note.setPinned(request.isPinned());
        note.setArchived(request.isArchived());
        note.setReminder(request.getReminder());

        if (request.getReminder() != null) {
            // Nếu người dùng đổi giờ nhắc khác giờ cũ -> Reset trạng thái gửi
            if (!request.getReminder().equals(note.getReminder())) {
                note.setReminder(request.getReminder());
                note.setReminderSent(false); // <--- Reset để gửi lại vào giờ mới
            }
        } else {
            // Nếu người dùng xóa giờ nhắc (gửi null lên)
            note.setReminder(null);
            note.setReminderSent(false); // Hoặc true cũng được, miễn là ko gửi
        }

        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email); // Hàm này đã kiểm tra quyền sở hữu

        // --- CẬP NHẬT LOGIC: Dọn dẹp trạng thái trước khi xóa ---
        note.setPinned(false);   // <--- Hủy ghim
        note.setArchived(false); // <--- (Khuyên dùng) Hủy luôn lưu trữ nếu có, để trạng thái sạch sẽ
        // --------------------------------------------------------

        note.setDeleted(true);   // Đưa vào thùng rác
        noteRepository.save(note);
    }

    // Xóa vĩnh viễn (Dùng trong màn hình Thùng rác -> Nút "Xóa ngay")
    @Override
    @Transactional
    public void hardDeleteNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email);

        // 2. THÊM ĐOẠN NÀY: Xóa tất cả file đính kèm trên ổ cứng trước
        if (note.getAttachments() != null && !note.getAttachments().isEmpty()) {
            for (com.example.noteapp.entity.Attachment attachment : note.getAttachments()) {
                fileStorageService.deleteFile(attachment.getFileName());
            }
        }

        // Sau đó mới xóa Note khỏi Database (lúc này Cascade sẽ tự xóa dòng trong bảng attachments)
        noteRepository.delete(note);
    }

    @Override
    @Transactional
    public void restoreNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email);
        note.setDeleted(false);
        noteRepository.save(note);
    }

    // Hàm phụ trợ để lấy User từ Email
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }

    @Override
    @Transactional
    public void addLabelToNote(Long noteId, Long labelId, String email) {
        // Lấy note và kiểm tra quyền sở hữu (đã có hàm này rồi)
        Note note = getNoteById(noteId, email);

        // Lấy label
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Nhãn không tồn tại"));

        // Quan trọng: Phải kiểm tra xem nhãn này có đúng là của user này không
        // (Tránh trường hợp user A lấy nhãn của user B gán vào note của mình)
        if (!label.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền sử dụng nhãn này");
        }

        // Thêm vào Set
        note.getLabels().add(label);
        noteRepository.save(note);
    }

    @Override
    @Transactional
    public void removeLabelFromNote(Long noteId, Long labelId, String email) {
        Note note = getNoteById(noteId, email);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Nhãn không tồn tại"));

        // Xóa khỏi Set
        note.getLabels().remove(label);
        noteRepository.save(note);
    }

    @Override
    @Transactional
    public void archiveNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email);

        // --- SỬA LOGIC LƯU TRỮ (TỐI ƯU UX) ---
        note.setArchived(true);
        note.setPinned(false); // <--- Bắt buộc hủy ghim khi lưu trữ
        note.setDeleted(false);
        // -------------------------------------

        noteRepository.save(note);
    }

    @Override
    @Transactional
    public void unarchiveNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email);
        note.setArchived(false);
        noteRepository.save(note);
    }
}