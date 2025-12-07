package com.example.noteapp.controller;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.dto.response.ApiResponse;
import com.example.noteapp.entity.Note;
import com.example.noteapp.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // Tạo ghi chú mới
    @PostMapping
    public ApiResponse<Note> createNote(@RequestBody NoteRequest request, Principal principal) {
        // principal.getName() sẽ trả về email (do cấu hình trong UserDetailsService)
        Note note = noteService.createNote(principal.getName(), request);
        return new ApiResponse<>(1000, "Tạo ghi chú thành công", note);
    }

    // Lấy danh sách ghi chú
    @GetMapping
    public ApiResponse<List<Note>> getAllNotes(Principal principal) {
        List<Note> notes = noteService.getAllNotes(principal.getName());
        return new ApiResponse<>(1000, "Lấy danh sách thành công", notes);
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
}