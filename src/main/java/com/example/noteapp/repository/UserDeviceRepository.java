package com.example.noteapp.repository;

import com.example.noteapp.entity.User;
import com.example.noteapp.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    // Tìm thiết bị dựa trên User và DeviceId
    Optional<UserDevice> findByUserAndDeviceId(User user, String deviceId);
}