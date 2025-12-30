package com.example.noteapp.service;

import com.example.noteapp.dto.request.ChangePasswordRequest;
import com.example.noteapp.dto.request.UpdateProfileRequest;
import com.example.noteapp.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // [SỬA] Nhận vào email và DTO request thay vì từng trường lẻ tẻ
    User updateProfile(String email, UpdateProfileRequest request);

    // [SỬA] Nhận vào email và DTO request
    void changePassword(String email, ChangePasswordRequest request);
}