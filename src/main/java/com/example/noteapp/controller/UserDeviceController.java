package com.example.noteapp.controller;

import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.dto.response.UserDeviceResponse;
import com.example.noteapp.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    // Xem danh sách thiết bị đang đăng nhập
    @GetMapping
    public ApiResponse<List<UserDeviceResponse>> getMyDevices(Principal principal) {
        return new ApiResponse<>(1000, "Danh sách thiết bị",
                userDeviceService.getAllDevices(principal.getName()));
    }

    // Đăng xuất khỏi một thiết bị (Xóa thiết bị đó)
    // DELETE /api/devices?deviceId=xyz-123
    @DeleteMapping
    public ApiResponse<Void> logoutDevice(@RequestParam String deviceId, Principal principal) {
        userDeviceService.logoutDevice(principal.getName(), deviceId);
        return new ApiResponse<>(1000, "Đăng xuất thiết bị thành công", null);
    }
}