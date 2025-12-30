package com.example.noteapp.controller;

import com.example.noteapp.dto.request.ChangePasswordRequest;
import com.example.noteapp.dto.request.UpdateProfileRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.service.FileStorageService;
import com.example.noteapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Quản lý thông tin cá nhân")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

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

        User updatedUser = userService.updateProfile(principal.getName(), request);

        return new ApiResponse<>(1000, "Cập nhật hồ sơ thành công", updatedUser);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Thay đổi mật khẩu đăng nhập")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        User user = getUserByEmail(principal.getName());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        userService.changePassword(principal.getName(), request);
        return new ApiResponse<>(1000, "Đổi mật khẩu thành công", null);
    }

    private User getUserByEmail(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }

    @PostMapping("/avatar")
    @Operation(summary = "Upload Avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        // Tận dụng FileStorageService đã có của bạn
        java.util.Map<?, ?> uploadResult = fileStorageService.uploadFile(file);
        String avatarUrl = (String) uploadResult.get("secure_url");

        // Gọi hàm updateProfile (đã có sẵn) để lưu URL
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl(avatarUrl);
        userService.updateProfile(principal.getName(), request);

        return new ApiResponse<>(1000, "Upload avatar thành công", avatarUrl);
    }
}