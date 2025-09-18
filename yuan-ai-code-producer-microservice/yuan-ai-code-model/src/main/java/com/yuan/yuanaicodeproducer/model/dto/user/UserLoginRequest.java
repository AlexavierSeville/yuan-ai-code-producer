package com.yuan.yuanaicodeproducer.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-19 11:38:30
 * @className UserLoginRequest
 * @description 用户登录
 */
@Data
@Schema(name = "UserLoginRequest", description = "用户登录请求")
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * 账号
     */
    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userAccount;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userPassword;
}

