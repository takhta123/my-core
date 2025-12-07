package com.example.noteapp.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;      // Ví dụ: 1000 là thành công
    private String message;
    private T result;
}