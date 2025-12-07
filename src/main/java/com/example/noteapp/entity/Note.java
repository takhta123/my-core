package com.example.noteapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob // Báo hiệu đây là trường văn bản lớn (LONGTEXT)
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private boolean isPinned;
    private boolean isArchived;
    private boolean isDeleted;

    private LocalDateTime reminder;

    @Column(name = "is_reminder_sent")
    private boolean isReminderSent = false;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // Liên kết ngược về User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Quan hệ nhiều-nhiều với Label (qua bảng trung gian note_labels)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "note_labels",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels;

    // Quan hệ 1-n với Checklist
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checklist> checklists;

    // Quan hệ 1-n với Attachment
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;
}