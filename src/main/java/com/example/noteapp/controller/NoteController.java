package com.example.noteapp.controller;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Note;
import com.example.noteapp.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Note Controller", description = "Quản lý các thao tác với Ghi chú")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ApiResponse<Page<Note>> getAllNotes(
            @RequestParam(defaultValue = "0") int page, // Mặc định trang 0
            @RequestParam(defaultValue = "10") int size, // Mặc định 10 note/trang
            Principal principal) {

        Page<Note> notes = noteService.getAllNotes(principal.getName(), page, size);
        return new ApiResponse<>(1000, "Danh sách ghi chú (phân trang)", notes);
    }

    // 2. Màn hình Lưu trữ
    @GetMapping("/archived")
    public ApiResponse<List<Note>> getArchivedNotes(Principal principal) {
        return new ApiResponse<>(1000, "Ghi chú đã lưu trữ", noteService.getArchivedNotes(principal.getName()));
    }

    // 3. Màn hình Thùng rác
    @GetMapping("/trash")
    public ApiResponse<List<Note>> getTrashedNotes(Principal principal) {
        return new ApiResponse<>(1000, "Thùng rác", noteService.getTrashedNotes(principal.getName()));
    }

    // 4. Tìm kiếm: GET /api/notes/search?keyword=abc
    @GetMapping("/search")
    public ApiResponse<List<Note>> searchNotes(@RequestParam String keyword, Principal principal) {
        return new ApiResponse<>(1000, "Kết quả tìm kiếm", noteService.searchNotes(principal.getName(), keyword));
    }

    // Tạo ghi chú mới
    @PostMapping
    @Operation(summary = "Tạo ghi chú mới", description = "API này dùng để user tạo một ghi chú mới")
    public ApiResponse<Note> createNote(@RequestBody NoteRequest request, Principal principal) {
        // principal.getName() sẽ trả về email (do cấu hình trong UserDetailsService)
        Note note = noteService.createNote(principal.getName(), request);
        return new ApiResponse<>(1000, "Tạo ghi chú thành công", note);
    }


    // Lấy chi tiết 1 ghi chú
    @GetMapping("/{id}")
    public ApiResponse<Note> getNoteById(@PathVariable Long id, Principal principal) {
        Note note = noteService.getNoteById(id, principal.getName());
        return new ApiResponse<>(1000, "Lấy chi tiết thành công", note);
    }

    // Cập nhật ghi chú
    @PutMapping("/{id}")
    public ApiResponse<Note> updateNote(@PathVariable Long id,
                                        @RequestBody NoteRequest request,
                                        Principal principal) {
        Note note = noteService.updateNote(id, principal.getName(), request);
        return new ApiResponse<>(1000, "Cập nhật thành công", note);
    }

    // Xóa ghi chú (Đưa vào thùng rác)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNote(@PathVariable Long id, Principal principal) {
        noteService.deleteNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã chuyển vào thùng rác", null);
    }

    // Khôi phục ghi chú
    @PutMapping("/{id}/restore")
    public ApiResponse<Void> restoreNote(@PathVariable Long id, Principal principal) {
        noteService.restoreNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã khôi phục ghi chú", null);
    }

    @PostMapping("/{noteId}/labels/{labelId}")
    public ApiResponse<Void> addLabelToNote(@PathVariable Long noteId,
                                            @PathVariable Long labelId,
                                            Principal principal) {
        noteService.addLabelToNote(noteId, labelId, principal.getName());
        return new ApiResponse<>(1000, "Đã gán nhãn thành công", null);
    }

    // 2. Gỡ nhãn khỏi ghi chú
    // DELETE /api/notes/1/labels/5
    @DeleteMapping("/{noteId}/labels/{labelId}")
    public ApiResponse<Void> removeLabelFromNote(@PathVariable Long noteId,
                                                 @PathVariable Long labelId,
                                                 Principal principal) {
        noteService.removeLabelFromNote(noteId, labelId, principal.getName());
        return new ApiResponse<>(1000, "Đã gỡ nhãn thành công", null);
    }

    // 1. Lưu trữ ghi chú
// PUT /api/notes/1/archive
    @PutMapping("/{id}/archive")
    public ApiResponse<Void> archiveNote(@PathVariable Long id, Principal principal) {
        noteService.archiveNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã lưu trữ ghi chú", null);
    }

    // 2. Hủy lưu trữ (Mang về màn hình chính)
// PUT /api/notes/1/unarchive
    @PutMapping("/{id}/unarchive")
    public ApiResponse<Void> unarchiveNote(@PathVariable Long id, Principal principal) {
        noteService.unarchiveNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã hủy lưu trữ", null);
    }

    // 3. Xóa vĩnh viễn: DELETE /api/notes/{id}/permanent
    @DeleteMapping("/{id}/permanent")
    public ApiResponse<Void> hardDeleteNote(@PathVariable Long id, Principal principal) {
        noteService.hardDeleteNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã xóa vĩnh viễn ghi chú", null);
    }
}