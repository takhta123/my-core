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
    private final FileStorageService fileStorageService;

    @PostMapping("/notes/{noteId}")
    @Operation(summary = "Upload file", description = "Tải file lên và đính kèm vào ghi chú (Dùng form-data)")
    public ApiResponse<Attachment> uploadFile(@PathVariable Long noteId,
                                              @RequestParam("file") MultipartFile file,
                                              Principal principal) {
        Attachment attachment = attachmentService.addAttachment(noteId, principal.getName(), file);
        return new ApiResponse<>(1000, "Upload thành công", attachment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file", description = "Xóa file đính kèm khỏi ghi chú và ổ cứng")
    public ApiResponse<Void> deleteFile(@PathVariable Long id, Principal principal) {
        attachmentService.deleteAttachment(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa file thành công", null);
    }

    @GetMapping("/files/{fileName:.+}")
    @Operation(summary = "Xem/Tải file", description = "API công khai để hiển thị ảnh hoặc tải file")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Xác định Content-Type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Mặc định nếu không xác định được
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}