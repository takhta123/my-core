package com.example.noteapp.controller;

import com.example.noteapp.dto.request.LabelRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Label;
import com.example.noteapp.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    // Lấy danh sách nhãn
    @GetMapping
    public ApiResponse<List<Label>> getAllLabels(Principal principal) {
        return new ApiResponse<>(1000, "Thành công", labelService.getAllLabels(principal.getName()));
    }

    // Tạo nhãn mới
    @PostMapping
    public ApiResponse<Label> createLabel(@RequestBody LabelRequest request, Principal principal) {
        return new ApiResponse<>(1000, "Tạo nhãn thành công", labelService.createLabel(principal.getName(), request));
    }

    // Xóa nhãn
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLabel(@PathVariable Long id, Principal principal) {
        labelService.deleteLabel(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa nhãn thành công", null);
    }
}