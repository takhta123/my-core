package com.example.noteapp.scheduler;

import com.example.noteapp.entity.Note;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final NoteRepository noteRepository;
    private final EmailService emailService;

    // Chạy mỗi 60 giây (60000 ms) một lần
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkReminders() {
        // 1. Lấy thời điểm hiện tại
        LocalDateTime now = LocalDateTime.now();

        // 2. Tìm các note đến hạn mà chưa báo
        List<Note> dueNotes = noteRepository.findByReminderBeforeAndIsReminderSentFalseAndIsDeletedFalse(now);

        if (!dueNotes.isEmpty()) {
            log.info("Tìm thấy {} ghi chú đến hạn nhắc nhở", dueNotes.size());
        }

        // 3. Duyệt và gửi mail
        for (Note note : dueNotes) {
            try {
                // Gửi email
                emailService.sendReminderEmail(note.getUser().getEmail(), note.getTitle(), note.getContent());

                // Đánh dấu là đã gửi để không gửi lại lần sau
                note.setReminderSent(true);

                // Lưu lại trạng thái
                noteRepository.save(note);

            } catch (Exception e) {
                log.error("Lỗi khi xử lý reminder cho note ID {}: {}", note.getId(), e.getMessage());
            }
        }
    }
}