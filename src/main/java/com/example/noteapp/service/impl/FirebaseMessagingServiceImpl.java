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
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Đã gửi Push Notification thành công: {}", response);

        } catch (Exception e) {
            log.error("Lỗi khi gửi Push Notification: {}", e.getMessage());
        }
    }
}