package com.example.noteapp.controller;

import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Attachment;
import com.example.noteapp.service.AttachmentService;
import com.example.noteapp.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment Management", description = "Upload và xem file đính kèm")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/notes/{noteId}")
    @Operation(summary = "Upload file (Cloudinary)", description = "Tải file lên Cloudinary và lưu link vào ghi chú")
    public ApiResponse<Attachment> uploadFile(@PathVariable Long noteId,
                                              @RequestParam("file") MultipartFile file,
                                              Principal principal) {
        Attachment attachment = attachmentService.addAttachment(noteId, principal.getName(), file);
        return new ApiResponse<>(1000, "Upload thành công", attachment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file", description = "Xóa file khỏi Cloudinary và Database")
    public ApiResponse<Void> deleteFile(@PathVariable Long id, Principal principal) {
        attachmentService.deleteAttachment(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa file thành công", null);
    }
}