package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.email = :email AND ev.verificationCode = :code")
    Optional<EmailVerification> findByEmailAndVerificationCode(@Param("email") String email, @Param("code") String verificationCode);
    
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.email = :email ORDER BY ev.id DESC")
    List<EmailVerification> findByEmailOrderByIdDesc(@Param("email") String email);
} 