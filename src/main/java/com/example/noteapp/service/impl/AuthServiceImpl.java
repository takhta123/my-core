package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.LoginRequest;
import com.example.noteapp.dto.request.RegisterRequest;
import com.example.noteapp.dto.request.VerifyRequest;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.entity.VerificationCode;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.repository.VerificationCodeRepository;
import com.example.noteapp.service.AuthService;
import com.example.noteapp.utils.JwtUtils;
import com.example.noteapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor // Tự động inject các repository và encoder (khỏi cần @Autowired)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false) // Chưa kích hoạt
                .build();

        User savedUser = userRepository.save(user);

        // Sinh code và lưu xuống DB (hàm này bạn đã có ở dưới)
        String code = generateVerificationCode(savedUser);

        // 2. THÊM DÒNG NÀY: Gửi email chứa mã code
        emailService.sendVerificationEmail(savedUser.getEmail(), code);

        return savedUser;
    }

    @Override
    @Transactional
    public void verifyAccount(VerifyRequest request) {
        // 1. Tìm user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 2. Tìm mã xác thực gần nhất trong DB (giả sử bạn có repository cho VerificationCode)
        VerificationCode vCode = verificationCodeRepository.findByUserIdAndCode(user.getId(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Mã xác thực không đúng"));

        // 3. Kiểm tra hết hạn
        if (vCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }

        // 4. Xác thực thành công -> Update User
        user.setEnabled(true);
        userRepository.save(user);

        // 5. Xóa mã cũ (tùy chọn, để sạch DB)
        verificationCodeRepository.deleteByUserId(user.getId());
    }

    @Override
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy email"));

        if (user.isEnabled()) { // SỬA: Check isEnabled cho đồng bộ
            throw new RuntimeException("Tài khoản đã được xác thực rồi");
        }

        // --- Logic chặn spam 60s ---
        Optional<VerificationCode> lastCodeOpt = verificationCodeRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());

        if (lastCodeOpt.isPresent()) {
            VerificationCode lastCode = lastCodeOpt.get();
            long secondsSinceLastSend = Duration.between(lastCode.getCreatedAt(), LocalDateTime.now()).getSeconds();

            if (secondsSinceLastSend < 60) {
                throw new RuntimeException("Vui lòng đợi " + (60 - secondsSinceLastSend) + " giây trước khi gửi lại.");
            }
        }

        // --- SỬA: Gọi hàm có tham số user ---
        String newCode = generateVerificationCode(user);

        // Gửi email
        emailService.sendVerificationEmail(email, newCode);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public String generateVerificationCode(User user) {
        String code = String.valueOf((int) ((Math.random() * 899999) + 100000)); // Sinh số 6 chữ số

        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .type("REGISTER")
                .createdAt(LocalDateTime.now())
                .build();

        verificationCodeRepository.save(verificationCode);
        return code;
    }
}