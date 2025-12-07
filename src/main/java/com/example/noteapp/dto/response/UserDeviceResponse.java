package com.example.noteapp.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDeviceResponse {
    private Long id;
    private String deviceName;
    private String deviceType;
    private LocalDateTime lastUsed;
    private String deviceId; // Để frontend biết ID nào mà gọi lệnh xóa
}