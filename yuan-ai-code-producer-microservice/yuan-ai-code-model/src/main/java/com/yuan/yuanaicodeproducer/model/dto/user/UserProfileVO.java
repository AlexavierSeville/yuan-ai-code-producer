package com.yuan.yuanaicodeproducer.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户个人信息视图对象
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Data
@Schema(name = "UserProfileVO", description = "用户个人信息视图对象")
public class UserProfileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userAccount;

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
     * 用户角色
     */
    @Schema(description = "用户角色")
    private String userRole;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
