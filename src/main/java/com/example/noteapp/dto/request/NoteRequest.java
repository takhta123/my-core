package com.example.noteapp.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteRequest {
    private String title;
    private String content;
    private String backgroundColor; // Mã màu (ví dụ: #FFFFFF)
    private boolean isPinned;
    private boolean isArchived;
    private LocalDateTime reminder; // Có thể null
}