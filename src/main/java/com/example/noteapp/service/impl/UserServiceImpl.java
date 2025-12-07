package com.example.noteapp.service.impl;

import com.example.noteapp.entity.User;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails; // Import mới
import org.springframework.security.core.userdetails.UserDetailsService; // Import mới
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import mới
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // Import mới
import java.util.Optional;

@Service
// 1. Thêm implements UserDetailsService
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateProfile(Long userId, String newFullName, String newAvatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (newFullName != null && !newFullName.isEmpty()) {
            user.setFullName(newFullName);
        }
        if (newAvatarUrl != null && !newAvatarUrl.isEmpty()) {
            user.setAvatarUrl(newAvatarUrl);
        }

        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 2. Thêm hàm loadUserByUsername bắt buộc của Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm user trong DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        // Convert User (Entity) sang UserDetails (Spring Security)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(), // Trạng thái kích hoạt (true/false)
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                new ArrayList<>() // Danh sách quyền (Roles). Vì User Entity chưa có Role, tạm thời để rỗng.
        );
    }
}