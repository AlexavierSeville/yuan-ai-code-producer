package com.yuan.yuanaicodeproducer.model.dto.user;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-19 11:12:57
 * @className UserRegisterRequest
 * @description 用户注册请求
 */
@Data
@Schema(name = "UserRegisterRequest", description = "用户注册请求，包含账号、密码与确认密码")
public class UserRegisterRequest implements Serializable {

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

    /**
     * 确认密码
     */
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String checkPassword;
}

