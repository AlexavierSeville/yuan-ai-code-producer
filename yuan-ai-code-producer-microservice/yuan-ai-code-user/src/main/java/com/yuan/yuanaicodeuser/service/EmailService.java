package com.yuan.yuanaicodeuser.service;

/**
 * 邮件服务接口
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     *
     * @param email 邮箱地址
     * @param verificationCode 验证码
     * @param codeType 验证码类型
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String verificationCode, String codeType);
}
