package com.yuan.yuanaicodeproducer.model.dto.user;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-19 17:24:12
 * @className UserAddRequest
 * @description 用户创建请求(管理员)
 */
@Data
@Schema(name = "UserAddRequest", description = "管理员新增用户请求")
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "账号（唯一）")
    private String userAccount;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像 URL")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Schema(description = "用户简介")
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    @Schema(description = "用户角色：user/admin")
    private String userRole;

    private static final long serialVersionUID = 3791271717373120793L;
}

