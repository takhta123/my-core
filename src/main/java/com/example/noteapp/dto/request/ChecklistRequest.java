package com.example.noteapp.dto.request;

import lombok.Data;

@Data
public class ChecklistRequest {
    private String content;
    private boolean isCompleted;
}