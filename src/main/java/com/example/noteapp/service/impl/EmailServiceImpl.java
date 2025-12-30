package com.example.noteapp.service.impl;

import com.example.noteapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là Bean để Spring quản lý
@RequiredArgsConstructor
@Slf4j // Để dùng log.info, log.error
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // Cấu hình nội dung email
            message.setFrom("NoteApp Support <noreply@noteapp.com>");
            message.setTo(toEmail);
            message.setSubject("Mã xác thực đăng ký tài khoản");
            message.setText("Xin chào,\n\nMã xác thực của bạn là: " + code +
                    "\n\nMã này sẽ hết hạn sau 15 phút. Vui lòng không chia sẻ mã này cho ai khác.");

            // Gửi email
            mailSender.send(message);
            log.info("Đã gửi email thành công đến: {}", toEmail);

        } catch (Exception e) {
            log.error("Lỗi khi gửi email đến {}: {}", toEmail, e.getMessage());
            // Tùy chọn: Có thể throw lỗi ra để AuthServiceImpl biết việc gửi mail thất bại
            // throw new RuntimeException("Gửi mail thất bại");
        }
    }

    @Async
    @Override
    public void sendReminderEmail(String toEmail, String noteTitle, String noteContent) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("NoteApp Support <noreply@noteapp.com>");
            message.setTo(toEmail);
            message.setSubject("Nhắc nhở: " + noteTitle);
            message.setText("Xin chào,\n\nBạn có một nhắc nhở cho ghi chú:\n\n"
                    + "Tiêu đề: " + noteTitle + "\n"
                    + "Nội dung: " + noteContent + "\n\n"
                    + "Hãy kiểm tra ứng dụng để xem chi tiết.");

            mailSender.send(message);
            log.info("Đã gửi reminder đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Lỗi gửi reminder: {}", e.getMessage());
        }
    }

    @Override
    public void sendResetPasswordEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("NoteApp Support <noreply@noteapp.com>");
            message.setTo(toEmail);
            message.setSubject("Yêu cầu đặt lại mật khẩu");
            message.setText("Xin chào,\n\nMã xác thực để đặt lại mật khẩu của bạn là: " + code +
                    "\n\nMã này sẽ hết hạn sau 15 phút.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi mail reset: {}", e.getMessage());
        }
    }
}