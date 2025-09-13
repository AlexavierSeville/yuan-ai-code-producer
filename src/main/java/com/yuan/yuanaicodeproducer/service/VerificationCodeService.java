package com.yuan.yuanaicodeproducer.service;

/**
 * 验证码服务接口
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
public interface VerificationCodeService {

    /**
     * 发送验证码
     *
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String codeType);

    /**
     * 验证验证码
     *
     * @param email 邮箱地址
     * @param verificationCode 验证码
     * @param codeType 验证码类型
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String verificationCode, String codeType);

    /**
     * 检查是否可以发送验证码（防止频繁发送）
     *
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 是否可以发送
     */
    boolean canSendCode(String email, String codeType);
}
