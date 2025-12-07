package com.example.noteapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    private String deviceId;   // Bắt buộc (để định danh thiết bị)
    private String deviceToken;// Tùy chọn (để gửi thông báo sau này)
    private String deviceType; // ANDROID / IOS
}