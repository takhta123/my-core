package com.example.noteapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChecklistRequest {
    @NotBlank(message = "Nội dung công việc không được để trống")
    private String content;
    private boolean isCompleted;
}