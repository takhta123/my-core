package com.example.noteapp.dto.request;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken; // Token nhận được từ Google/Firebase ở Client
    private String deviceId; // Để lưu device như login thường
    private String deviceToken;
    private String deviceType;
}