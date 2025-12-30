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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Avatar với tọa độ cắt")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("w") int w,
            @RequestParam("h") int h,
            Principal principal) {

        // 1. Upload ảnh GỐC lên Cloudinary
        java.util.Map<?, ?> uploadResult = fileStorageService.uploadFile(file);
        String originalUrl = (String) uploadResult.get("secure_url");

        // 2. Biến đổi URL gốc thành URL đã cắt (Transformation URL)
        // URL gốc: https://.../upload/v1234/id.jpg
        // URL mới: https://.../upload/x_10,y_10,w_200,h_200,c_crop/v1234/id.jpg

        String transformation = String.format("x_%d,y_%d,w_%d,h_%d,c_crop", x, y, w, h);
        String avatarUrl = originalUrl.replace("/upload/", "/upload/" + transformation + "/");

        // 3. Lưu URL đã biến đổi vào Database
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl(avatarUrl);
        userService.updateProfile(principal.getName(), request);

        return new ApiResponse<>(1000, "Upload avatar thành công", avatarUrl);
    }
}