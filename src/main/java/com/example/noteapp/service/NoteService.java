package com.example.noteapp.service;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.entity.Note;
import java.util.List;

public interface NoteService {
    // Tạo ghi chú mới cho User có email cụ thể
    Note createNote(String email, NoteRequest request);

    // Lấy tất cả ghi chú của User
    List<Note> getAllNotes(String email);

    // Lấy chi tiết 1 ghi chú
    Note getNoteById(Long noteId, String email);

    // Cập nhật ghi chú
    Note updateNote(Long noteId, String email, NoteRequest request);

    // Xóa ghi chú (Xóa mềm - đưa vào thùng rác)
    void deleteNote(Long noteId, String email);

    // Khôi phục ghi chú từ thùng rác
    void restoreNote(Long noteId, String email);
}