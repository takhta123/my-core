package com.example.noteapp.repository;

import com.example.noteapp.entity.Checklist; // Đảm bảo tên Class này khớp với tên file Entity của bạn
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    // 1. Tìm tất cả các mục checklist thuộc về một Note cụ thể
    // (Spring sẽ tìm cột note_id trong bảng checklist)
    List<Checklist> findByNoteId(Long noteId);

    // 2. Xóa tất cả checklist của một Note (Dùng khi người dùng xóa Note đó đi)
    @Modifying
    @Transactional
    void deleteByNoteId(Long noteId);
}