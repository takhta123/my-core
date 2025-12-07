package com.example.noteapp.service;

public interface FirebaseMessagingService {
    void sendNotification(String token, String title, String body);
}