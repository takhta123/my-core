package com.example.noteapp.service;

import com.example.noteapp.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User updateProfile(Long userId, String newFullName, String newAvatarUrl);
    void changePassword(Long userId, String oldPassword, String newPassword);
}