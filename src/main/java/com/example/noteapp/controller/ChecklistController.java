package com.example.noteapp.controller;

import com.example.noteapp.dto.request.ChecklistRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Checklist;
import com.example.noteapp.service.ChecklistService;
import io.swagger.v3.oas.annotations.Operation; // Import
import io.swagger.v3.oas.annotations.tags.Tag; // Import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Checklist Management", description = "Quản lý các mục công việc trong Ghi chú")
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping("/notes/{noteId}/checklists")
    @Operation(summary = "Thêm mục công việc", description = "Thêm một dòng to-do vào trong ghi chú cụ thể")
    public ApiResponse<Checklist> addChecklist(@PathVariable Long noteId,
                                               @RequestBody @Valid ChecklistRequest request,
                                               Principal principal) {
        Checklist item = checklistService.addChecklistItem(noteId, principal.getName(), request);
        return new ApiResponse<>(1000, "Thêm mục thành công", item);
    }

    @PutMapping("/checklists/{id}")
    @Operation(summary = "Cập nhật mục công việc", description = "Sửa nội dung hoặc đánh dấu hoàn thành/chưa hoàn thành")
    public ApiResponse<Checklist> updateChecklist(@PathVariable Long id,
                                                  @RequestBody @Valid ChecklistRequest request,
                                                  Principal principal) {
        Checklist item = checklistService.updateChecklistItem(id, principal.getName(), request);
        return new ApiResponse<>(1000, "Cập nhật thành công", item);
    }

    @DeleteMapping("/checklists/{id}")
    @Operation(summary = "Xóa mục công việc", description = "Xóa hoàn toàn một dòng checklist khỏi ghi chú")
    public ApiResponse<Void> deleteChecklist(@PathVariable Long id, Principal principal) {
        checklistService.deleteChecklistItem(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa thành công", null);
    }
}