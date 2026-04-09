package com.example.noteapp.service;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String code);
    void sendReminderEmail(String toEmail, String noteTitle, String noteContent);
    void sendResetPasswordEmail(String toEmail, String code);
}