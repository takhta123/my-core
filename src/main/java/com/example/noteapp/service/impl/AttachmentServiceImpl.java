package com.example.noteapp.service.impl;

import com.example.noteapp.entity.Attachment;
import com.example.noteapp.entity.Note;
import com.example.noteapp.repository.AttachmentRepository;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.service.AttachmentService;
import com.example.noteapp.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final NoteRepository noteRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Attachment addAttachment(Long noteId, String email, MultipartFile file) {
        // 1. Kiểm tra Note
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note không tồn tại"));

        if (!note.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        // 2. Upload file lên Cloudinary (THAY ĐỔI Ở ĐÂY)
        Map uploadResult = fileStorageService.uploadFile(file);

        // Lấy thông tin từ kết quả trả về của Cloudinary
        String fileUrl = (String) uploadResult.get("secure_url"); // Link ảnh HTTPS
        String publicId = (String) uploadResult.get("public_id"); // ID định danh để xóa sau này

        // 3. Lưu thông tin vào DB
        Attachment attachment = new Attachment();
        attachment.setFileName(publicId); // Lưu Public ID vào cột fileName
        attachment.setFilePath(fileUrl);  // Lưu URL tuyệt đối vào cột filePath
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setNote(note);

        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId, String email) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));

        if (!attachment.getNote().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền xóa");
        }

        // Xóa file trên Cloudinary (Dùng Public ID đang lưu trong fileName)
        fileStorageService.deleteFile(attachment.getFileName());

        // Xóa trong DB
        attachmentRepository.delete(attachment);
    }

    @Override
    public Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}