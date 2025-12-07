package com.example.noteapp.service;

import com.example.noteapp.dto.request.LoginRequest;
import com.example.noteapp.dto.request.RegisterRequest;
import com.example.noteapp.dto.request.VerifyRequest;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;

public interface AuthService {
    void verifyAccount(VerifyRequest request);
    void resendVerificationCode(String email);

    User register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String generateVerificationCode(User user);

}