package com.example.noteapp.controller;

import com.example.noteapp.dto.request.LoginRequest;
import com.example.noteapp.dto.request.RegisterRequest;
import com.example.noteapp.dto.request.VerifyRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        User result = authService.register(request);

        return ApiResponse.<User>builder()
                .code(1000)
                .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực.")
                .result(result)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse result = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng nhập thành công")
                .result(result)
                .build();
    }

    // --- ĐÃ SỬA: Gộp lại thành 1 hàm verify duy nhất, dùng VerifyRequest ---
    @PostMapping("/verify")
    public ApiResponse<Void> verify(@RequestBody VerifyRequest request) {
        authService.verifyAccount(request);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xác thực tài khoản thành công!")
                .build();
    }

    // --- ĐÃ SỬA: Chuyển sang dùng ApiResponse cho đồng bộ ---
    @PostMapping("/resend-code")
    public ApiResponse<Void> resendCode(@RequestParam String email) {
        authService.resendVerificationCode(email);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã gửi lại mã xác thực mới. Vui lòng kiểm tra email.")
                .build();
    }
}