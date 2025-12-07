package com.example.noteapp.service;

import com.example.noteapp.dto.request.*;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;

public interface AuthService {
    void verifyAccount(VerifyRequest request);
    void resendVerificationCode(String email);
    void forgotPassword(String email);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse loginWithGoogle(GoogleLoginRequest request);

    User register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String generateVerificationCode(User user);

}