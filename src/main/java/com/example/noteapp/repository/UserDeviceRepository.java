package com.example.noteapp.repository;

import com.example.noteapp.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    // Tìm tất cả thiết bị của một user (để gửi thông báo cho tất cả máy của họ)
    List<UserDevice> findByUserId(Long userId);

    // Tìm device theo token (để check trùng hoặc update)
    Optional<UserDevice> findByDeviceId(String deviceId);
}