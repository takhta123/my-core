package com.example.noteapp.controller;

import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.dto.response.UserDeviceResponse;
import com.example.noteapp.service.UserDeviceService;
import io.swagger.v3.oas.annotations.Operation; // Import
import io.swagger.v3.oas.annotations.tags.Tag; // Import
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Device Management", description = "Quản lý danh sách thiết bị đăng nhập & Đăng xuất từ xa")
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping
    @Operation(summary = "Xem danh sách thiết bị", description = "Lấy danh sách tất cả các thiết bị đang đăng nhập vào tài khoản này")
    public ApiResponse<List<UserDeviceResponse>> getMyDevices(Principal principal) {
        return new ApiResponse<>(1000, "Danh sách thiết bị",
                userDeviceService.getAllDevices(principal.getName()));
    }

    @DeleteMapping
    @Operation(summary = "Đăng xuất thiết bị (Từ xa)", description = "Xóa thông tin phiên đăng nhập của một thiết bị cụ thể (dựa vào Device ID)")
    public ApiResponse<Void> logoutDevice(@RequestParam String deviceId, Principal principal) {
        userDeviceService.logoutDevice(principal.getName(), deviceId);
        return new ApiResponse<>(1000, "Đăng xuất thiết bị thành công", null);
    }
}