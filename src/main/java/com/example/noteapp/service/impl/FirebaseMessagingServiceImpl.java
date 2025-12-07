package com.example.noteapp.service.impl;

import com.example.noteapp.service.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    @Override
    public void sendNotification(String token, String title, String body) {
        try {
            // 1. Tạo nội dung thông báo (Notification Payload)
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 2. Tạo Message chứa Token thiết bị và Notification
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    // .putData("key", "value") // Có thể thêm dữ liệu tùy chỉnh ở đây
                    .build();

            // 3. Gửi đi qua Firebase
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Đã gửi Push Notification thành công: {}", response);

        } catch (Exception e) {
            log.error("Lỗi khi gửi Push Notification: {}", e.getMessage());
        }
    }
}