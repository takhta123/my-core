package com.example.noteapp.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword; // Nhập lại mật khẩu mới (để Frontend check khớp)
}