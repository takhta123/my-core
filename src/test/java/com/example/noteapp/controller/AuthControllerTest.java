package com.example.noteapp.controller;

import com.example.noteapp.dto.request.LoginRequest;
import com.example.noteapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// --- CHÚ Ý: GÓI MỚI CỦA SPRING BOOT 4 ---
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
// Thay vì gói cũ: org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc // Annotation này giờ sẽ hoạt động nhờ import đúng gói
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private AuthService authService;

    // Nếu @Autowired ObjectMapper vẫn lỗi, hãy dùng cách khởi tạo thủ công:
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_validation_fail_when_email_empty() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword("123456");

        String jsonContent = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email không được để trống"));
    }
}