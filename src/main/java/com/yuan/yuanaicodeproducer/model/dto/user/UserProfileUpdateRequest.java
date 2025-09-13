package com.yuan.yuanaicodeproducer.model.dto.user;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户个人信息更新请求
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Data
@Schema(name = "UserProfileUpdateRequest", description = "用户个人信息更新请求")
public class UserProfileUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Schema(description = "用户简介")
    private String userProfile;

    /**
     * 用户密码（可选，用于修改密码）
     */
    @Schema(description = "用户密码（可选，用于修改密码）")
    private String userPassword;

    /**
     * 确认密码（修改密码时必填）
     */
    @Schema(description = "确认密码（修改密码时必填）")
    private String checkPassword;
}
