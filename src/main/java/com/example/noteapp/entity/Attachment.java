package com.example.noteapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attachments") @Data
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;

    @ManyToOne @JoinColumn(name = "note_id")
    @JsonIgnore
    private Note note;
}
