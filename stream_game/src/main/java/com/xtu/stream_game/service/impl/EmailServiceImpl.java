package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.EmailVerification;
import com.xtu.stream_game.repository.EmailVerificationRepository;
import com.xtu.stream_game.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    
    private static final String FROM_EMAIL = "2320499203@qq.com";
    private static final int CODE_LENGTH = 6;

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        // 保存验证码到数据库
        EmailVerification verification = new EmailVerification(email, verificationCode);
        emailVerificationRepository.save(verification);
        
        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("游戏平台邮箱验证");
        message.setText("您的验证码是: " + verificationCode + "\n验证码有效期为15分钟。");
        
        mailSender.send(message);
    }

    @Override
    public boolean verifyEmail(String email, String verificationCode) {
        EmailVerification verification = emailVerificationRepository
            .findByEmailAndVerificationCode(email, verificationCode)
            .orElse(null);
            
        if (verification == null || verification.isExpired()) {
            return false;
        }
        
        verification.setVerified(true);
        emailVerificationRepository.save(verification);
        return true;
    }
    
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 