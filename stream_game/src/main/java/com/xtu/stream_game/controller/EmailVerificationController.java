package com.xtu.stream_game.controller;

import com.xtu.stream_game.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("邮箱地址不能为空");
        }

        try {
            logger.info("开始发送验证码到邮箱: {}", email);
            String verificationCode = emailService.generateVerificationCode();
            logger.info("生成验证码: {}", verificationCode);
            
            emailService.sendVerificationEmail(email, verificationCode);
            
            logger.info("验证码发送成功");
            Map<String, String> response = new HashMap<>();
            response.put("message", "验证码已发送到您的邮箱");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("发送验证码失败: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "发送验证码失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null) {
            logger.warn("邮箱或验证码为空");
            return ResponseEntity.badRequest().body("邮箱和验证码不能为空");
        }

        try {
            logger.info("验证邮箱: {}, 验证码: {}", email, code);
            boolean isValid = emailService.verifyEmail(email, code);
            logger.info("验证结果: {}", isValid ? "成功" : "失败");
            
            Map<String, Object> response = new HashMap<>();
            response.put("verified", isValid);
            response.put("message", isValid ? "验证成功" : "验证码无效或已过期");
            response.put("email", email);
            response.put("code", code);
            
            if (!isValid) {
                logger.warn("验证失败，可能的原因: 验证码无效、已过期或已被使用");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("验证失败: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "验证失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/status/{email}")
    public ResponseEntity<?> getVerificationStatus(@PathVariable String email) {
        try {
            boolean isVerified = emailService.isEmailVerified(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("verified", isVerified);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取验证状态失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("获取验证状态失败：" + e.getMessage());
        }
    }
} 