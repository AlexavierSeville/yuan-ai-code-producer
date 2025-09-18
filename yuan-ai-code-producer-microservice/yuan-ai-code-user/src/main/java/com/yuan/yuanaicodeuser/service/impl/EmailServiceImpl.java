package com.yuan.yuanaicodeuser.service.impl;

import com.yuan.yuanaicodeproducer.model.enums.VerificationCodeTypeEnum;
import com.yuan.yuanaicodeuser.service.EmailService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Service
//@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean sendVerificationCode(String email, String verificationCode, String codeType) {
        try {
            // 检查邮件配置是否正确
            if (fromEmail.contains("your-") || fromEmail.contains("example")) {
                log.warn("邮件服务未正确配置，验证码：{}，邮箱：{}，类型：{}", verificationCode, email, codeType);
                return true;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            
            // 根据验证码类型设置不同的邮件主题和内容
            VerificationCodeTypeEnum typeEnum = VerificationCodeTypeEnum.getEnumByValue(codeType);
            String typeText = typeEnum != null ? typeEnum.getText() : "验证";
            
            message.setSubject("元仔AI应用生成 - " + typeText + "验证码");
            
            String content = String.format(
                "您好！\n\n" +
                "您的%s验证码是：%s\n\n" +
                "验证码有效期为5分钟，请及时使用。\n" +
                "如非本人操作，请忽略此邮件。\n\n" +
                "元仔AI应用生成团队",
                typeText, verificationCode
            );
            
            message.setText(content);
            
            mailSender.send(message);
            log.info("验证码邮件发送成功，邮箱：{}，验证码类型：{}", email, codeType);
            return true;
        } catch (Exception e) {
            log.error("验证码邮件发送失败，邮箱：{}，验证码类型：{}，错误：{}", email, codeType, e.getMessage(), e);
            
            // 生产环境：如果邮件发送失败，返回失败
            // 开发环境：返回成功并打印验证码到日志
            String activeProfile = System.getProperty("spring.profiles.active", "local");
            if ("prod".equals(activeProfile)) {
                return false;
            } else {
                log.warn("开发环境测试模式：验证码为 {}，请手动输入", verificationCode);
                return true;
            }
        }
    }
}
