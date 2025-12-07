package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.ChecklistRequest;
import com.example.noteapp.entity.Checklist;
import com.example.noteapp.entity.Note;
import com.example.noteapp.repository.ChecklistRepository;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional
    public Checklist addChecklistItem(Long noteId, String email, ChecklistRequest request) {
        // 1. Tìm Note và check quyền sở hữu
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Ghi chú không tồn tại"));

        if (!note.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền truy cập ghi chú này");
        }

        // 2. Tạo Checklist mới
        Checklist item = new Checklist();
        item.setContent(request.getContent());
        item.setCompleted(request.isCompleted());
        item.setNote(note);

        // Logic đơn giản: Đặt vị trí xuống cuối cùng
        item.setPosition(note.getChecklists().size());

        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public Checklist updateChecklistItem(Long checklistId, String email, ChecklistRequest request) {
        Checklist item = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("Mục không tồn tại"));

        // Check quyền sở hữu thông qua Note cha
        if (!item.getNote().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền chỉnh sửa");
        }

        item.setContent(request.getContent());
        item.setCompleted(request.isCompleted());

        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteChecklistItem(Long checklistId, String email) {
        Checklist item = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("Mục không tồn tại"));

        if (!item.getNote().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền xóa");
        }

        checklistRepository.delete(item);
    }

    @Override
    public List<Checklist> getChecklistsByNoteId(Long noteId, String email) {
        // Logic check quyền tương tự...
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if(!note.getUser().getEmail().equals(email)) throw new RuntimeException("Unauthorized");

        return checklistRepository.findByNoteId(noteId);
    }
}