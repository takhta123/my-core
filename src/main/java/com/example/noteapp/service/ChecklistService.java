package com.example.noteapp.service;

import com.example.noteapp.dto.request.ChecklistRequest;
import com.example.noteapp.entity.Checklist;
import java.util.List;

public interface ChecklistService {
    // Thêm mục checklist vào ghi chú
    Checklist addChecklistItem(Long noteId, String email, ChecklistRequest request);

    // Cập nhật mục checklist (Sửa nội dung hoặc tick xong)
    Checklist updateChecklistItem(Long checklistId, String email, ChecklistRequest request);

    // Xóa mục checklist
    void deleteChecklistItem(Long checklistId, String email);

    // Lấy danh sách (thường ít dùng lẻ vì nó đi kèm Note, nhưng cứ viết cho đủ bộ)
    List<Checklist> getChecklistsByNoteId(Long noteId, String email);
}