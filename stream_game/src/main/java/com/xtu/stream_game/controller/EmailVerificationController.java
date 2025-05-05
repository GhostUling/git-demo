package com.xtu.stream_game.controller;

import com.xtu.stream_game.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("邮箱地址不能为空");
        }

        try {
            String verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationEmail(email, verificationCode);
            Map<String, String> response = new HashMap<>();
            response.put("message", "验证码已发送到您的邮箱");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("发送验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body("邮箱和验证码不能为空");
        }

        try {
            boolean isValid = emailService.verifyEmail(email, code);
            Map<String, Object> response = new HashMap<>();
            response.put("verified", isValid);
            response.put("message", isValid ? "验证成功" : "验证码无效或已过期");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("验证失败：" + e.getMessage());
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
            return ResponseEntity.badRequest().body("获取验证状态失败：" + e.getMessage());
        }
    }
} 