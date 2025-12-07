package com.example.noteapp.service;

import com.example.noteapp.dto.response.UserDeviceResponse;
import java.util.List;

public interface UserDeviceService {
    List<UserDeviceResponse> getAllDevices(String email);
    void logoutDevice(String email, String deviceId);
}