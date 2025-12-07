package com.example.noteapp.controller;

import com.example.noteapp.dto.request.ChangePasswordRequest;
import com.example.noteapp.dto.request.UpdateProfileRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Quản lý thông tin cá nhân")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Xem thông tin cá nhân", description = "Lấy thông tin của người dùng đang đăng nhập")
    public ApiResponse<User> getMyInfo(Principal principal) {
        User user = getUserByEmail(principal.getName());
        return new ApiResponse<>(1000, "Lấy thông tin thành công", user);
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật hồ sơ", description = "Thay đổi Tên hiển thị, Avatar...")
    public ApiResponse<User> updateProfile(@RequestBody @Valid UpdateProfileRequest request, Principal principal) {
        User user = getUserByEmail(principal.getName());

        User updatedUser = userService.updateProfile(
                user.getId(),
                request.getFullName(),
                request.getAvatarUrl()
        );

        return new ApiResponse<>(1000, "Cập nhật hồ sơ thành công", updatedUser);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Thay đổi mật khẩu đăng nhập")
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

    private User getUserByEmail(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }
}