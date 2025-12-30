package com.example.noteapp.controller;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Note;
import com.example.noteapp.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Note Management", description = "Quản lý Ghi chú (CRUD, Tìm kiếm, Lưu trữ, Thùng rác)")
public class NoteController {

    private final NoteService noteService;

    // --- API CHÍNH: Vừa lấy danh sách, vừa tìm kiếm ---
    @GetMapping
    @Operation(summary = "Lấy danh sách ghi chú", description = "Lấy danh sách (có phân trang) hoặc tìm kiếm nếu có param 'search'")
    public ApiResponse<Page<Note>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search, // Param này dùng để tìm kiếm
            Principal principal) {

        // Hàm này trong Service đã xử lý logic: Nếu có 'search' thì tìm, không thì lấy tất cả
        Page<Note> notes = noteService.getAllNotes(principal.getName(), page, size, search);
        return new ApiResponse<>(1000, "Danh sách ghi chú", notes);
    }

    // --- ĐÃ XÓA ĐOẠN @GetMapping("/search") GÂY LỖI ---
    // Lý do: Logic tìm kiếm đã được gộp vào API @GetMapping ở trên.

    @PostMapping
    @Operation(summary = "Tạo ghi chú mới", description = "Tạo một ghi chú mới với tiêu đề, nội dung, màu sắc...")
    public ApiResponse<Note> createNote(@RequestBody NoteRequest request, Principal principal) {
        Note note = noteService.createNote(principal.getName(), request);
        return new ApiResponse<>(1000, "Tạo ghi chú thành công", note);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết ghi chú", description = "Lấy toàn bộ thông tin của một ghi chú theo ID")
    public ApiResponse<Note> getNoteById(@PathVariable Long id, Principal principal) {
        Note note = noteService.getNoteById(id, principal.getName());
        return new ApiResponse<>(1000, "Lấy chi tiết thành công", note);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật ghi chú", description = "Sửa tiêu đề, nội dung, màu sắc, ghim...")
    public ApiResponse<Note> updateNote(@PathVariable Long id,
                                        @RequestBody NoteRequest request,
                                        Principal principal) {
        Note note = noteService.updateNote(id, principal.getName(), request);
        return new ApiResponse<>(1000, "Cập nhật thành công", note);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa ghi chú (Xóa mềm)", description = "Chuyển ghi chú vào thùng rác (Soft Delete)")
    public ApiResponse<Void> deleteNote(@PathVariable Long id, Principal principal) {
        noteService.deleteNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã chuyển vào thùng rác", null);
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Khôi phục ghi chú", description = "Khôi phục ghi chú từ thùng rác về màn hình chính")
    public ApiResponse<Void> restoreNote(@PathVariable Long id, Principal principal) {
        noteService.restoreNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã khôi phục ghi chú", null);
    }

    @PostMapping("/{noteId}/labels/{labelId}")
    @Operation(summary = "Gán nhãn", description = "Dán một nhãn (Label) vào ghi chú")
    public ApiResponse<Void> addLabelToNote(@PathVariable Long noteId,
                                            @PathVariable Long labelId,
                                            Principal principal) {
        noteService.addLabelToNote(noteId, labelId, principal.getName());
        return new ApiResponse<>(1000, "Đã gán nhãn thành công", null);
    }

    @DeleteMapping("/{noteId}/labels/{labelId}")
    @Operation(summary = "Gỡ nhãn", description = "Gỡ bỏ một nhãn khỏi ghi chú")
    public ApiResponse<Void> removeLabelFromNote(@PathVariable Long noteId,
                                                 @PathVariable Long labelId,
                                                 Principal principal) {
        noteService.removeLabelFromNote(noteId, labelId, principal.getName());
        return new ApiResponse<>(1000, "Đã gỡ nhãn thành công", null);
    }

    @GetMapping("/label/{labelId}")
    @Operation(summary = "Lấy ghi chú theo nhãn", description = "Lấy danh sách ghi chú thuộc một nhãn cụ thể (bao gồm cả lưu trữ)")
    public ApiResponse<Page<Note>> getNotesByLabel(
            @PathVariable Long labelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        Page<Note> notes = noteService.getNotesByLabel(labelId, principal.getName(), page, size);
        return new ApiResponse<>(1000, "Thành công", notes);
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Lưu trữ ghi chú", description = "Chuyển ghi chú vào mục Archive")
    public ApiResponse<Void> archiveNote(@PathVariable Long id, Principal principal) {
        noteService.archiveNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã lưu trữ ghi chú", null);
    }

    @PutMapping("/{id}/unarchive")
    @Operation(summary = "Hủy lưu trữ", description = "Chuyển ghi chú từ Archive về màn hình chính")
    public ApiResponse<Void> unarchiveNote(@PathVariable Long id, Principal principal) {
        noteService.unarchiveNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã hủy lưu trữ", null);
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Xóa vĩnh viễn", description = "Xóa hoàn toàn ghi chú và file đính kèm khỏi hệ thống (Không thể khôi phục)")
    public ApiResponse<Void> hardDeleteNote(@PathVariable Long id, Principal principal) {
        noteService.hardDeleteNote(id, principal.getName());
        return new ApiResponse<>(1000, "Đã xóa vĩnh viễn ghi chú", null);
    }

    @GetMapping("/archived")
    public ResponseEntity<ApiResponse<Page<Note>>> getArchivedNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Note> notes = noteService.getArchivedNotes(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<Note>>builder()
                .result(notes)
                .code(1000)
                .message("Lấy danh sách lưu trữ thành công")
                .build());
    }

    @GetMapping("/trash")
    public ResponseEntity<ApiResponse<Page<Note>>> getTrashedNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Note> notes = noteService.getTrashedNotes(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<Note>>builder()
                .result(notes)
                .code(1000)
                .message("Lấy danh sách thùng rác thành công")
                .build());
    }

    @GetMapping("/reminders")
    @Operation(summary = "Lấy danh sách lời nhắc", description = "Lấy các ghi chú có đặt giờ nhắc nhở, sắp xếp theo thời gian nhắc tới gần nhất")
    public ApiResponse<Page<Note>> getNotesWithReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        Page<Note> notes = noteService.getNotesWithReminders(principal.getName(), page, size);
        return new ApiResponse<>(1000, "Danh sách lời nhắc", notes);
    }
}