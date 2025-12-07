package com.example.noteapp.repository;

import com.example.noteapp.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // Lấy danh sách file đính kèm của một Note
    List<Attachment> findByNoteId(Long noteId);

    // Xóa tất cả file đính kèm khi xóa Note
    @Modifying
    @Transactional
    void deleteByNoteId(Long noteId);
}