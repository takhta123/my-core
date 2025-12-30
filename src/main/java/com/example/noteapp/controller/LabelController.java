package com.example.noteapp.controller;

import com.example.noteapp.dto.request.LabelRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Label;
import com.example.noteapp.service.LabelService;
import io.swagger.v3.oas.annotations.Operation; // Import
import io.swagger.v3.oas.annotations.tags.Tag; // Import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
@Tag(name = "Label Management", description = "Quản lý Nhãn (Tạo, Xóa, Lấy danh sách)")
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    @Operation(summary = "Lấy danh sách nhãn", description = "Trả về tất cả các nhãn do người dùng tạo")
    public ApiResponse<List<Label>> getAllLabels(Principal principal) {
        return new ApiResponse<>(1000, "Thành công", labelService.getAllLabels(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Tạo nhãn mới", description = "Tạo một nhãn mới để phân loại ghi chú (Tên nhãn là duy nhất)")
    public ApiResponse<Label> createLabel(@RequestBody @Valid LabelRequest request, Principal principal) {
        return new ApiResponse<>(1000, "Tạo nhãn thành công", labelService.createLabel(principal.getName(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhãn", description = "Xóa nhãn khỏi hệ thống (đồng thời gỡ nhãn này khỏi các ghi chú liên quan)")
    public ApiResponse<Void> deleteLabel(@PathVariable Long id, Principal principal) {
        labelService.deleteLabel(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa nhãn thành công", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Label> updateLabel(@PathVariable Long id, @RequestBody @Valid LabelRequest request, Principal principal) {
        // Bạn cần implement hàm updateLabel trong Service tương tự createLabel
        // Logic: Tìm label theo ID -> Set name mới -> Save
        Label updatedLabel = labelService.updateLabel(id, request.getName(), principal.getName());
        return new ApiResponse<>(1000, "Cập nhật nhãn thành công", updatedLabel);
    }
}