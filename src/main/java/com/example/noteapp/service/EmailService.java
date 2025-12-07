package com.example.noteapp.service;

public interface EmailService {
    // Chỉ khai báo tên hàm, không có phần thân {}
    void sendVerificationEmail(String toEmail, String code);
}