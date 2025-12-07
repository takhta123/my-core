package com.example.noteapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelRequest {
    @NotBlank(message = "Tên nhãn không được để trống")
    @Size(max = 20, message = "Tên nhãn không được quá 20 ký tự")
    private String name;
}