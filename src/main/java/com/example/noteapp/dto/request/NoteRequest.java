package com.example.noteapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty; // Thêm import này
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteRequest {
    private String title;
    private String content;
    private String backgroundColor;

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("isArchived")
    private boolean isArchived;

    // -----------------------------------------------------------------------

    private LocalDateTime reminder;
}