package com.example.noteapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "labels") @Data
public class Label {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
}