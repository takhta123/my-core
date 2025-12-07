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

    // Token dùng để gửi thông báo (FCM Token)
    private String token;

    // ID định danh thiết bị (ví dụ: Android ID, iOS UUID) để không lưu trùng
    private String deviceId;

    // Loại thiết bị (ANDROID, IOS, WEB)
    private String deviceType;

    private LocalDateTime lastLoggedIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}