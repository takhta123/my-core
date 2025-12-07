package com.example.noteapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity @Table(name = "user_devices") @Data
public class UserDevice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private String deviceName;
    private LocalDateTime lastUsed;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
}
