package com.example.noteapp.controller;

import com.example.noteapp.dto.request.ChecklistRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Checklist;
import com.example.noteapp.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    // Thêm mục vào Note: POST /api/notes/{noteId}/checklists
    @PostMapping("/notes/{noteId}/checklists")
    public ApiResponse<Checklist> addChecklist(@PathVariable Long noteId,
                                               @RequestBody ChecklistRequest request,
                                               Principal principal) {
        Checklist item = checklistService.addChecklistItem(noteId, principal.getName(), request);
        return new ApiResponse<>(1000, "Thêm mục thành công", item);
    }

    // Sửa mục: PUT /api/checklists/{id}
    @PutMapping("/checklists/{id}")
    public ApiResponse<Checklist> updateChecklist(@PathVariable Long id,
                                                  @RequestBody ChecklistRequest request,
                                                  Principal principal) {
        Checklist item = checklistService.updateChecklistItem(id, principal.getName(), request);
        return new ApiResponse<>(1000, "Cập nhật thành công", item);
    }

    // Xóa mục: DELETE /api/checklists/{id}
    @DeleteMapping("/checklists/{id}")
    public ApiResponse<Void> deleteChecklist(@PathVariable Long id, Principal principal) {
        checklistService.deleteChecklistItem(id, principal.getName());
        return new ApiResponse<>(1000, "Xóa thành công", null);
    }
}