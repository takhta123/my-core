package com.example.noteapp.scheduler;

import com.example.noteapp.entity.Note;
import com.example.noteapp.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrashScheduler {

    private final NoteRepository noteRepository;

    // Chạy mỗi ngày một lần vào lúc 00:00 (cron = "giây phút giờ ngày tháng thứ")
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupTrash() {
        // Tính mốc thời gian: Lấy thời điểm hiện tại trừ đi 7 ngày
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Tìm các ghi chú trong thùng rác cũ hơn 7 ngày
        List<Note> expiredNotes = noteRepository.findByIsDeletedTrueAndUpdatedAtBefore(sevenDaysAgo);

        if (!expiredNotes.isEmpty()) {
            noteRepository.deleteAll(expiredNotes); // Xóa cứng vĩnh viễn
            System.out.println("Đã dọn dẹp " + expiredNotes.size() + " ghi chú quá hạn trong thùng rác.");
        }
    }
}