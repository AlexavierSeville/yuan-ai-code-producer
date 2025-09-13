package com.yuan.yuanaicodeproducer.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.service.EmailService;
import com.yuan.yuanaicodeproducer.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;
    // 发送间隔（分钟）
    private static final int SEND_INTERVAL_MINUTES = 3;
    // Redis key前缀
    private static final String CODE_KEY_PREFIX = "verification_code:";
    private static final String SEND_TIME_KEY_PREFIX = "send_time:";

    @Override
    public boolean sendVerificationCode(String email, String codeType) {
        // 1. 参数校验
        if (StrUtil.hasBlank(email, codeType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 2. 检查是否可以发送验证码
        if (!canSendCode(email, codeType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发送过于频繁，请" + SEND_INTERVAL_MINUTES + "分钟后再试");
        }

        // 3. 生成验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        // 4. 存储验证码到Redis
        String codeKey = CODE_KEY_PREFIX + codeType + ":" + email;
        redisTemplate.opsForValue().set(codeKey, verificationCode, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 5. 记录发送时间到Redis
        String sendTimeKey = SEND_TIME_KEY_PREFIX + codeType + ":" + email;
        redisTemplate.opsForValue().set(sendTimeKey, String.valueOf(System.currentTimeMillis()), SEND_INTERVAL_MINUTES, TimeUnit.MINUTES);

        // 6. 发送邮件
        boolean sendResult = emailService.sendVerificationCode(email, verificationCode, codeType);
        if (!sendResult) {
            // 发送失败，删除Redis中的验证码
            redisTemplate.delete(codeKey);
            redisTemplate.delete(sendTimeKey);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "验证码发送失败");
        }

        log.info("验证码发送成功，邮箱：{}，验证码类型：{}", email, codeType);
        return true;
    }

    @Override
    public boolean verifyCode(String email, String verificationCode, String codeType) {
        // 1. 参数校验
        if (StrUtil.hasBlank(email, verificationCode, codeType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 2. 从Redis获取验证码
        String codeKey = CODE_KEY_PREFIX + codeType + ":" + email;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        
        if (StrUtil.isBlank(storedCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不存在或已过期");
        }

        // 3. 验证验证码
        if (!verificationCode.equals(storedCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        // 4. 验证成功后删除验证码
        redisTemplate.delete(codeKey);

        log.info("验证码验证成功，邮箱：{}，验证码类型：{}", email, codeType);
        return true;
    }

    @Override
    public boolean canSendCode(String email, String codeType) {
        String sendTimeKey = SEND_TIME_KEY_PREFIX + codeType + ":" + email;
        String lastSendTimeStr = redisTemplate.opsForValue().get(sendTimeKey);
        
        if (StrUtil.isBlank(lastSendTimeStr)) {
            return true; // 没有发送过验证码，可以发送
        }

        // 检查是否在发送间隔内
        long lastSendTime = Long.parseLong(lastSendTimeStr);
        long currentTime = System.currentTimeMillis();
        long intervalMillis = SEND_INTERVAL_MINUTES * 60 * 1000L;
        
        return (currentTime - lastSendTime) >= intervalMillis;
    }
}
