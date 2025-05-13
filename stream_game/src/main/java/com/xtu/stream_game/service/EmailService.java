package com.xtu.stream_game.service;

public interface EmailService {
    /**
     * 发送验证码邮件
     * @param email 目标邮箱
     * @param verificationCode 验证码
     */
    void sendVerificationEmail(String email, String verificationCode);

    /**
     * 验证邮箱验证码
     * @param email 邮箱
     * @param verificationCode 验证码
     * @return 验证是否成功
     */
    boolean verifyEmail(String email, String verificationCode);

    /**
     * 检查邮箱验证码是否有效，但不修改验证状态
     * @param email 邮箱
     * @param verificationCode 验证码
     * @return 验证码是否有效
     */
    boolean checkEmailAndVerificationCode(String email, String verificationCode);

    /**
     * 检查邮箱是否已验证
     * @param email 邮箱
     * @return 是否已验证
     */
    boolean isEmailVerified(String email);

    /**
     * 发送通知邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendNotificationEmail(String to, String subject, String content);

    /**
     * 生成验证码
     * @return 生成的验证码
     */
    String generateVerificationCode();
} 