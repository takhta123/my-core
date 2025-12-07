package com.example.noteapp.controller;

import com.example.noteapp.dto.request.ChangePasswordRequest;
import com.example.noteapp.dto.request.UpdateProfileRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Lấy thông tin user đang đăng nhập
    @GetMapping("/me")
    public ApiResponse<User> getMyInfo(Principal principal) {
        User user = getUserByEmail(principal.getName());
        return new ApiResponse<>(1000, "Lấy thông tin thành công", user);
    }

    // 2. Cập nhật hồ sơ (Tên, Avatar)
    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@RequestBody @Valid UpdateProfileRequest request, Principal principal) {
        User user = getUserByEmail(principal.getName());

        User updatedUser = userService.updateProfile(
                user.getId(),
                request.getFullName(),
                request.getAvatarUrl()
        );

        return new ApiResponse<>(1000, "Cập nhật hồ sơ thành công", updatedUser);
    }

    // 3. Đổi mật khẩu
    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        User user = getUserByEmail(principal.getName());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        userService.changePassword(
                user.getId(),
                request.getOldPassword(),
                request.getNewPassword()
        );

        return new ApiResponse<>(1000, "Đổi mật khẩu thành công", null);
    }

    // Hàm phụ trợ giúp tìm User từ Email trong Token
    private User getUserByEmail(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }
}