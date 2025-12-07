package com.example.noteapp.repository;

import com.example.noteapp.entity.User;
import com.example.noteapp.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user bằng email (Dùng cho chức năng đăng nhập)
    // Spring sẽ tự hiểu thành: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Kiểm tra xem email đã tồn tại chưa (Dùng khi đăng ký để tránh trùng lặp)
    Boolean existsByEmail(String email);

}
