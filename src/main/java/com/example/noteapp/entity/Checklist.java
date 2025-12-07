package com.example.noteapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- Import này
import jakarta.persistence.*;
import lombok.Data; // <--- Thêm Lombok Data để tự sinh getter/setter

@Entity
@Table(name = "checklists")
@Data // <--- Đừng quên cái này, nếu không code sẽ không lấy được dữ liệu
public class Checklist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(name = "is_completed") // Map đúng tên cột trong DB
    private boolean isCompleted;

    private int position; // Để sắp xếp thứ tự (nếu cần)

    @ManyToOne
    @JoinColumn(name = "note_id")
    @JsonIgnore // <--- QUAN TRỌNG: Ngắt vòng lặp ở đây
    private Note note;
}