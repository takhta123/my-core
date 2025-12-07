package com.example.noteapp.repository;

import com.example.noteapp.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // 1. Lấy ghi chú cho Màn hình chính (Chưa xóa, Chưa lưu trữ)
    List<Note> findByUserIdAndIsDeletedFalseAndIsArchivedFalseOrderByCreatedAtDesc(Long userId);

    // 2. Lấy ghi chú Lưu trữ (Chưa xóa, Đã lưu trữ)
    List<Note> findByUserIdAndIsDeletedFalseAndIsArchivedTrueOrderByCreatedAtDesc(Long userId);

    // 3. Lấy ghi chú trong Thùng rác (Đã xóa)
    List<Note> findByUserIdAndIsDeletedTrueOrderByCreatedAtDesc(Long userId);

    // 4. Tìm kiếm (Chỉ tìm trong các ghi chú chưa bị xóa vĩnh viễn)
    // Thêm chữ IgnoreCase vào cuối
    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.isDeleted = false AND (n.title LIKE CONCAT('%', :keyword, '%') OR n.content LIKE CONCAT('%', :keyword, '%'))")
    List<Note> searchNotes(@Param("userId") Long userId, @Param("keyword") String keyword);
}