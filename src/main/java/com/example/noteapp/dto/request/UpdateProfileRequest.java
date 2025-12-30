// src/main/java/com/example/noteapp/dto/request/UpdateProfileRequest.java
package com.example.noteapp.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String avatarUrl;

    // Thêm các trường mới
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
}