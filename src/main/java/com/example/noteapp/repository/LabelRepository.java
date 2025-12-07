package com.example.noteapp.repository;

import com.example.noteapp.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    // Lấy danh sách Label của một User cụ thể
    // (Giả sử Label có trường userId hoặc quan hệ với User)
    List<Label> findByUserId(Long userId);
}