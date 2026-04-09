package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.*;
import com.example.noteapp.dto.response.AuthResponse;
import com.example.noteapp.entity.User;
import com.example.noteapp.entity.VerificationCode;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.repository.VerificationCodeRepository;
import com.example.noteapp.service.AuthService;
import com.example.noteapp.utils.JwtUtils;
import com.example.noteapp.service.EmailService;
import com.example.noteapp.entity.UserDevice;
import com.example.noteapp.repository.UserDeviceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final UserDeviceRepository userDeviceRepository;

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
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        String code = generateVerificationCode(savedUser);
        emailService.sendVerificationEmail(savedUser.getEmail(), code);

        return savedUser;
    }

    @Override
    @Transactional
    public void verifyAccount(VerifyRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        VerificationCode vCode = verificationCodeRepository.findByUserIdAndCode(user.getId(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Mã xác thực không đúng"));

        if (vCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }

        user.setEnabled(true);
        userRepository.save(user);

        verificationCodeRepository.deleteByUserId(user.getId());
    }

    @Override
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy email"));

        if (user.isEnabled()) {
            throw new RuntimeException("Tài khoản đã được xác thực rồi, không cần gửi lại mã.");
        }

        Optional<VerificationCode> lastCodeOpt = verificationCodeRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());

        if (lastCodeOpt.isPresent()) {
            VerificationCode lastCode = lastCodeOpt.get();
            long secondsSinceLastSend = Duration.between(lastCode.getCreatedAt(), LocalDateTime.now()).getSeconds();

            if (secondsSinceLastSend < 60) {
                throw new RuntimeException("Vui lòng đợi " + (60 - secondsSinceLastSend) + " giây trước khi gửi lại.");
            }
        }

        String newCode = generateVerificationCode(user);

        emailService.sendVerificationEmail(email, newCode);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        if (request.getDeviceId() != null && !request.getDeviceId().isEmpty()) {
            saveUserDevice(user, request);
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
        return generateVerificationCode(user, "REGISTER");
    }

    private String generateVerificationCode(User user, String type) {
        String code = String.valueOf((int) ((Math.random() * 899999) + 100000));
        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
        verificationCodeRepository.save(verificationCode);
        return code;
    }

    private void saveUserDevice(User user, LoginRequest request) {

        Optional<UserDevice> existingDevice = userDeviceRepository.findByUserAndDeviceId(user, request.getDeviceId());

        if (existingDevice.isPresent()) {

            UserDevice device = existingDevice.get();
            device.setLastUsed(LocalDateTime.now());
            device.setToken(request.getDeviceToken());

            userDeviceRepository.save(device);
        } else {

            UserDevice newDevice = UserDevice.builder()
                    .user(user)
                    .deviceId(request.getDeviceId())
                    .deviceType(request.getDeviceType())
                    .token(request.getDeviceToken())
                    .deviceName("Unknown Device")
                    .lastUsed(LocalDateTime.now())
                    .build();

            userDeviceRepository.save(newDevice);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        String code = generateVerificationCode(user, "RESET_PASSWORD");
        emailService.sendResetPasswordEmail(email, code);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        VerificationCode vCode = verificationCodeRepository.findByUserIdAndCode(user.getId(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Mã xác thực không đúng"));

        if (!"RESET_PASSWORD".equals(vCode.getType()) || vCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực không hợp lệ hoặc đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        verificationCodeRepository.delete(vCode);
    }

    @Override
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        try {

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String avatarUrl = decodedToken.getPicture();

            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {

                user = User.builder()
                        .email(email)
                        .fullName(name)
                        .avatarUrl(avatarUrl)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .enabled(true)
                        .build();
                user = userRepository.save(user);
            }

            if (request.getDeviceId() != null) {
                LoginRequest loginReq = LoginRequest.builder()
                        .deviceId(request.getDeviceId())
                        .deviceToken(request.getDeviceToken())
                        .deviceType(request.getDeviceType())
                        .build();
                saveUserDevice(user, loginReq);
            }

            String token = jwtUtils.generateToken(user.getEmail());
            return AuthResponse.builder().token(token).authenticated(true).build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
    }
}