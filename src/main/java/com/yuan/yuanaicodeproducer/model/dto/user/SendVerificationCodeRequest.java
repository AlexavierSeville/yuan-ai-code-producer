package com.yuan.yuanaicodeproducer.model.dto.user;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-01-09
 * @className SendVerificationCodeRequest
 * @description 发送验证码请求
 */
@Data
@Schema(name = "SendVerificationCodeRequest", description = "发送验证码请求")
public class SendVerificationCodeRequest implements Serializable {

    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /**
     * 验证码类型
     */
    @Schema(description = "验证码类型：REGISTER-注册，RESET_PASSWORD-重置密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codeType;
}
