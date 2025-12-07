package com.example.noteapp.controller;

import com.example.noteapp.dto.request.*;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation; // Import này
import io.swagger.v3.oas.annotations.tags.Tag; // Import này
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API Đăng ký, Đăng nhập và Xác thực tài khoản") // <--- Tên nhóm
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới và gửi mã xác thực qua Email") // <--- Mô tả API
    public ApiResponse<User> register(@RequestBody @Valid RegisterRequest request) {
        User result = authService.register(request);

        return ApiResponse.<User>builder()
                .code(1000)
                .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực.")
                .result(result)
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Kiểm tra email/password và trả về JWT Token + Lưu thông tin thiết bị")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse result = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng nhập thành công")
                .result(result)
                .build();
    }

    @PostMapping("/verify")
    @Operation(summary = "Xác thực tài khoản (OTP)", description = "Nhập mã code 6 số gửi về email để kích hoạt tài khoản")
    public ApiResponse<Void> verify(@RequestBody VerifyRequest request) {
        authService.verifyAccount(request);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xác thực tài khoản thành công!")
                .build();
    }

    @PostMapping("/resend-code")
    @Operation(summary = "Gửi lại mã OTP", description = "Gửi lại mã xác thực mới nếu mã cũ hết hạn (giới hạn 60s/lần)")
    public ApiResponse<Void> resendCode(@RequestParam String email) {
        authService.resendVerificationCode(email);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã gửi lại mã xác thực mới. Vui lòng kiểm tra email.")
                .build();
    }

    // ... (Các API cũ)

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu", description = "Gửi email chứa mã xác thực để đặt lại mật khẩu")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return new ApiResponse<>(1000, "Đã gửi mã xác thực qua email", null);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu", description = "Dùng mã xác thực để thiết lập mật khẩu mới")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new ApiResponse<>(1000, "Đặt lại mật khẩu thành công", null);
    }

    @PostMapping("/google")
    @Operation(summary = "Đăng nhập bằng Google", description = "Gửi ID Token từ Firebase Client lên để đăng nhập/đăng ký tự động")
    public ApiResponse<AuthResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        return new ApiResponse<>(1000, "Đăng nhập Google thành công", authService.loginWithGoogle(request));
    }
}