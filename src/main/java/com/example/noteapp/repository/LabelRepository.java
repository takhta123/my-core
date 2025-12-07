package com.example.noteapp.repository;

import com.example.noteapp.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findByUserId(Long userId);

    // --- THÊM DÒNG NÀY ---
    // Kiểm tra xem user này đã có nhãn tên này chưa
    boolean existsByUserIdAndName(Long userId, String name);
}