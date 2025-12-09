package com.example.noteapp.repository;

import com.example.noteapp.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // 1. Sửa hàm này: Trả về Page thay vì List, và nhận thêm tham số Pageable
    // Lưu ý: Đã bỏ phần "OrderByCreatedAtDesc" trong tên hàm vì Pageable sẽ lo việc sắp xếp
    Page<Note> findByUserIdAndIsDeletedFalseAndIsArchivedFalse(Long userId, Pageable pageable);
    Page<Note> findByUserIdAndIsArchivedTrueAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<Note> findByUserIdAndIsDeletedTrue(Long userId, Pageable pageable);

    // Bạn cũng có thể làm tương tự cho Archive và Trash nếu muốn (để sau cũng được)
    List<Note> findByUserIdAndIsDeletedFalseAndIsArchivedTrueOrderByCreatedAtDesc(Long userId);
    List<Note> findByUserIdAndIsDeletedTrueOrderByCreatedAtDesc(Long userId);

    // 4. Tìm kiếm (Chỉ tìm trong các ghi chú chưa bị xóa vĩnh viễn)
    // Thêm chữ IgnoreCase vào cuối
    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.isDeleted = false AND (n.title LIKE CONCAT('%', :keyword, '%') OR n.content LIKE CONCAT('%', :keyword, '%'))")
    List<Note> searchNotes(@Param("userId") Long userId, @Param("keyword") String keyword);

    List<Note> findByReminderBeforeAndIsReminderSentFalseAndIsDeletedFalse(LocalDateTime now);
}