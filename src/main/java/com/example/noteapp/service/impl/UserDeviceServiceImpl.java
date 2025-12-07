package com.example.noteapp.service.impl;

import com.example.noteapp.dto.response.UserDeviceResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.entity.UserDevice;
import com.example.noteapp.repository.UserDeviceRepository;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserDeviceResponse> getAllDevices(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserDevice> devices = userDeviceRepository.findByUserId(user.getId());

        return devices.stream().map(device -> UserDeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .deviceId(device.getDeviceId())
                .lastUsed(device.getLastUsed())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logoutDevice(String email, String deviceIdInput) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm thiết bị cụ thể của user này
        UserDevice device = userDeviceRepository.findByUserAndDeviceId(user, deviceIdInput)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại hoặc không thuộc về bạn"));

        // Xóa khỏi DB (Đăng xuất)
        userDeviceRepository.delete(device);
    }
}