package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.entity.Note;
import com.example.noteapp.entity.User;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

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
                .isDeleted(false) // Mặc định không nằm trong thùng rác
                .user(user)       // Gán user sở hữu
                .build();

        return noteRepository.save(note);
    }

    @Override
    public List<Note> getAllNotes(String email) {
        User user = getUserByEmail(email);
        // Gọi hàm repository bạn đã có sẵn
        return noteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
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

        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId, String email) {
        Note note = getNoteById(noteId, email);
        note.setDeleted(true); // Soft delete: Chỉ đánh dấu là đã xóa
        noteRepository.save(note);
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
}