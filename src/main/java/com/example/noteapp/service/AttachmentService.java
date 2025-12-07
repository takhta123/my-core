package com.example.noteapp.service;

import com.example.noteapp.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment addAttachment(Long noteId, String email, MultipartFile file);
    void deleteAttachment(Long attachmentId, String email);
    Attachment getAttachmentById(Long attachmentId);
}