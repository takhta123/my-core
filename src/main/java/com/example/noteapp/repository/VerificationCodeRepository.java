package com.example.noteapp.repository;

import com.example.noteapp.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    // Tìm mã xác thực trong database để so sánh với mã người dùng nhập
    Optional<VerificationCode> findByCode(String code);

    // Tìm mã theo User ID (để xem user này có đang chờ xác thực không)
    Optional<VerificationCode> findByUserId(Long userId);

    // Trong VerificationCodeRepository.java
    Optional<VerificationCode> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<VerificationCode> findByUserIdAndCode(Long userId, String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.user.id = :userId")
    void deleteByUserId(Long userId);
}