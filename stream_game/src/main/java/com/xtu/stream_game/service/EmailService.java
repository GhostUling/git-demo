package com.xtu.stream_game.service;

public interface EmailService {
    void sendVerificationEmail(String email, String verificationCode);
    boolean verifyEmail(String email, String verificationCode);
} 