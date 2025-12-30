package com.example.noteapp.scheduler;

import com.example.noteapp.entity.Note;
import com.example.noteapp.repository.NoteRepository;
import com.example.noteapp.service.EmailService; // Hoặc FirebaseMessagingService
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final NoteRepository noteRepository;
    private final EmailService emailService; // Giả sử bạn gửi qua Email

    // Chạy mỗi 60 giây (60000ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Note> dueNotes = noteRepository.findAllDueReminders(now);

        for (Note note : dueNotes) {
            try {
                // 1. Gửi thông báo (Email hoặc Push Notification)
                String subject = "Nhắc nhở: " + (note.getTitle() != null ? note.getTitle() : "Ghi chú không tên");
                String content = "Đã đến giờ cho ghi chú của bạn:\n" + note.getContent();

                // emailService.sendSimpleMessage(note.getUser().getEmail(), subject, content);
                // Hoặc gọi firebaseService.sendNotification(...)

                System.out.println("Đã gửi nhắc nhở cho Note ID: " + note.getId());

                // 2. Cập nhật trạng thái để không gửi lại
                note.setReminderSent(true);
                noteRepository.save(note);

            } catch (Exception e) {
                System.err.println("Lỗi gửi nhắc nhở: " + e.getMessage());
            }
        }
    }
}