package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.EmailVerification;
import com.xtu.stream_game.repository.EmailVerificationRepository;
import com.xtu.stream_game.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

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
        logger.debug("验证邮箱: {}, 验证码: {}", email, verificationCode);
        
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
            .findByEmailAndVerificationCode(email, verificationCode);
            
        if (verificationOpt.isEmpty()) {
            logger.debug("未找到匹配的验证记录");
            return false;
        }
        
        EmailVerification verification = verificationOpt.get();
        logger.debug("找到验证记录: ID={}, 过期时间={}, 是否已验证={}", 
                    verification.getId(), verification.getExpiryTime(), verification.isVerified());
        
        if (verification.isExpired()) {
            logger.debug("验证码已过期");
            return false;
        }
        
        if (verification.isVerified()) {
            logger.debug("验证码已被使用");
            return false;
        }
        
        // 标记为已验证
        verification.setVerified(true);
        emailVerificationRepository.save(verification);
        logger.debug("验证成功并已更新数据库");
        return true;
    }
    
    @Override
    public boolean checkEmailAndVerificationCode(String email, String verificationCode) {
        logger.debug("检查邮箱验证码(不更改状态): {}, 验证码: {}", email, verificationCode);
        
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
            .findByEmailAndVerificationCode(email, verificationCode);
            
        if (verificationOpt.isEmpty()) {
            logger.debug("未找到匹配的验证记录");
            return false;
        }
        
        EmailVerification verification = verificationOpt.get();
        logger.debug("找到验证记录: ID={}, 过期时间={}, 是否已验证={}", 
                    verification.getId(), verification.getExpiryTime(), verification.isVerified());
        
        if (verification.isExpired()) {
            logger.debug("验证码已过期");
            return false;
        }
        
        // 注意：这里不检查是否已验证，也不修改验证状态
        logger.debug("验证码检查通过，不改变验证状态");
        return true;
    }
    
    @Override
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmailOrderByIdDesc(email)
                .stream()
                .findFirst()
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    @Override
    public void sendNotificationEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        
        mailSender.send(message);
    }
    
    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 