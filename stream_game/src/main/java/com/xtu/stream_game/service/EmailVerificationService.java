package com.xtu.stream_game.service;

public interface EmailVerificationService {
    /**
     * 发送验证码到指定邮箱
     * @param email 目标邮箱地址
     * @throws Exception 发送失败时抛出异常
     */
    void sendVerificationCode(String email) throws Exception;

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @return 验证是否成功
     * @throws Exception 验证失败时抛出异常
     */
    boolean verifyCode(String email, String code) throws Exception;

    /**
     * 检查邮箱是否已验证
     * @param email 邮箱地址
     * @return 是否已验证
     * @throws Exception 检查失败时抛出异常
     */
    boolean isEmailVerified(String email) throws Exception;
} 