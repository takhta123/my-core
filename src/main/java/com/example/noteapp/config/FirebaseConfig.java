package com.example.noteapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Đảm bảo bạn đã copy file serviceAccountKey.json vào thư mục resources
        ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");

        // Kiểm tra nếu file không tồn tại (để tránh lỗi khi chưa cấu hình)
        if (!resource.exists()) {
            // Trả về null hoặc log cảnh báo nếu chưa có file key (để app vẫn chạy được)
            System.out.println("CẢNH BÁO: Chưa tìm thấy file serviceAccountKey.json. Chức năng Push Notification sẽ không hoạt động.");
            return null;
        }

        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        // Kiểm tra để tránh khởi tạo lại nếu đã tồn tại
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}