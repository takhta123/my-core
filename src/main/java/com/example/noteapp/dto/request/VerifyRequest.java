package com.example.noteapp.dto.request;

import lombok.Data;

@Data
public class VerifyRequest {
    private String email;
    private String code;
}
