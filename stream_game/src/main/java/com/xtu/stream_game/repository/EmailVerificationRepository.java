package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    List<EmailVerification> findByEmailOrderByIdDesc(String email);
} 