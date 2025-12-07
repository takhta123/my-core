package com.example.noteapp.repository;

import com.example.noteapp.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Lấy tất cả ghi chú của một User cụ thể (Sắp xếp theo ngày tạo giảm dần)
    // Giả sử trong Entity Note bạn có field tên là 'user' hoặc 'userId'
    List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Tìm kiếm ghi chú theo tiêu đề (Dùng cho thanh search)
    // Tương đương: SELECT * FROM notes WHERE title LIKE %keyword% AND user_id = ?
    List<Note> findByUserIdAndTitleContaining(Long userId, String title);
}