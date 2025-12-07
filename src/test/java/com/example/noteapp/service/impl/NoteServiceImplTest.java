package com.example.noteapp.service.impl;

import org.junit.jupiter.api.Test;
import com.example.noteapp.dto.request.NoteRequest;
import com.example.noteapp.entity.Note;
import com.example.noteapp.entity.User;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {
    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private NoteRequest noteRequest;
    private User user;
    private Note note;

    @BeforeEach
    void initData() {
        // Chuẩn bị dữ liệu giả
        noteRequest = new NoteRequest();
        noteRequest.setTitle("Test Note");
        noteRequest.setContent("Content");

        user = User.builder().id(1L).email("test@gmail.com").build();

        note = Note.builder()
                .id(10L)
                .title("Test Note")
                .content("Content")
                .user(user)
                .build();
    }

    @Test
    void createNote_success() {
        // GIVEN: Giả lập hành vi của Repository
        Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        Mockito.when(noteRepository.save(any(Note.class))).thenReturn(note);

        // WHEN: Gọi hàm cần test
        Note result = noteService.createNote("test@gmail.com", noteRequest);

        // THEN: Kiểm tra kết quả
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Note", result.getTitle());
    }

    @Test
    void createNote_userNotFound_fail() {
        // GIVEN: Giả lập không tìm thấy user
        Mockito.when(userRepository.findByEmail("wrong@gmail.com")).thenReturn(Optional.empty());

        // WHEN & THEN: Mong đợi ném ra lỗi RuntimeException
        Assertions.assertThrows(RuntimeException.class, () -> {
            noteService.createNote("wrong@gmail.com", noteRequest);
        });
    }
}