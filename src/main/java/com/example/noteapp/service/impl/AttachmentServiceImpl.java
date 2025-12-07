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

        // 2. Lưu file vật lý
        String fileName = fileStorageService.storeFile(file);

        // 3. Tạo đường dẫn tải về (URL)
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/attachments/files/")
                .path(fileName)
                .toUriString();

        // 4. Lưu thông tin vào DB
        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFilePath(fileDownloadUri); // Lưu URL để Frontend dễ dùng
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

        // Xóa file vật lý trước
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