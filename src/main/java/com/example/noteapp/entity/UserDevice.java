package com.example.noteapp.entity; // Đổi package theo project của bạn

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    // ID định danh thiết bị
    private String deviceId;

    // Tên thiết bị (Ví dụ: iPhone 13)
    private String deviceName;

    // Loại thiết bị (ANDROID, IOS, WEB)
    private String deviceType;

    // Thời gian đăng nhập cuối cùng
    private LocalDateTime lastUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}