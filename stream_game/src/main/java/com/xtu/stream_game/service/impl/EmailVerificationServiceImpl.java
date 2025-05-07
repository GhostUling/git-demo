package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.EmailVerification;
import com.xtu.stream_game.repository.EmailVerificationRepository;
import com.xtu.stream_game.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private EmailServiceImpl emailService;

    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;

    @Override
    public void sendVerificationCode(String email) throws Exception {
        // 生成6位数字验证码
        String verificationCode = generateVerificationCode();
        
        // 创建新的验证记录
        EmailVerification verification = new EmailVerification(email, verificationCode);
        
        // 保存验证记录
        emailVerificationRepository.save(verification);
        
        // 发送验证码邮件
        emailService.sendVerificationEmail(email, verificationCode);
    }

    @Override
    public boolean verifyCode(String email, String code) throws Exception {
        EmailVerification verification = emailVerificationRepository.findByEmailOrderByIdDesc(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new Exception("未找到验证记录"));

        if (verification.isExpired()) {
            throw new Exception("验证码已过期");
        }

        if (verification.isVerified()) {
            throw new Exception("该验证码已被使用");
        }

        boolean isValid = verification.getVerificationCode().equals(code);
        if (isValid) {
            verification.setVerified(true);
            emailVerificationRepository.save(verification);
        }

        return isValid;
    }

    @Override
    public boolean isEmailVerified(String email) throws Exception {
        return emailVerificationRepository.findByEmailOrderByIdDesc(email)
                .stream()
                .findFirst()
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
} 