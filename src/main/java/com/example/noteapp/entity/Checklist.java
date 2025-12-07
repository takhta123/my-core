package com.example.noteapp.entity;

import jakarta.persistence.*;

@Entity // <--- QUAN TRỌNG: Bạn đang thiếu dòng này
@Table(name = "checklists") // Tên bảng trong database

public class Checklist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private boolean isCompleted;
    private int position;

    @ManyToOne @JoinColumn(name = "note_id")
    private Note note;
}
