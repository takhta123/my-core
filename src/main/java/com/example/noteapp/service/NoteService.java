package com.example.noteapp.service;

import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.entity.Note;
import com.example.noteapp.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NoteService {
    // Tạo ghi chú mới cho User có email cụ thể
    Note createNote(String email, NoteRequest request);

    // Lấy tất cả ghi chú của User
    Page<Note> getAllNotes(String email, int page, int size);
    Page<Note> getArchivedNotes(int page, int size); // Lấy danh sách lưu trữ
    Page<Note> getTrashedNotes(int page, int size);
    Page<Note> getNotesWithReminders(String email, int page, int size);
    Page<Note> getNotesByLabel(Long labelId, String email, int page, int size);

    List<Note> getArchivedNotes(String email);
    List<Note> getTrashedNotes(String email);
    List<Note> searchNotes(String email, String keyword);


    Note getNoteById(Long noteId, String email);
    Note updateNote(Long noteId, String email, NoteRequest request);
    void deleteNote(Long noteId, String email);
    void hardDeleteNote(Long noteId, String email);
    void archiveNote(Long noteId, String email);
    void unarchiveNote(Long noteId, String email);
    void restoreNote(Long noteId, String email);
    void addLabelToNote(Long noteId, Long labelId, String email);
    void removeLabelFromNote(Long noteId, Long labelId, String email);

}