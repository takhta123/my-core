package com.example.noteapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty; // Thêm import này
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteRequest {
    private String title;
    private String content;
    private String backgroundColor;

    // --- SỬA Ở ĐÂY: Thêm @JsonProperty để map đúng tên trường từ Frontend ---

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("isArchived")
    private boolean isArchived;

    // -----------------------------------------------------------------------

    private LocalDateTime reminder;
}