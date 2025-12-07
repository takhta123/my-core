package com.example.noteapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteRequest {
    private String title;
    private String content;

    private String backgroundColor;
    private boolean isPinned;
    private boolean isArchived;
    private LocalDateTime reminder;
}