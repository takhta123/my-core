package com.example.noteapp.config; // Đổi package cho đúng với project của bạn

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Cho phép tất cả các đường dẫn
                        .allowedOrigins("http://localhost:5173") // Chỉ cho phép React chạy ở port 5173 gọi vào
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các method cho phép
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }


}