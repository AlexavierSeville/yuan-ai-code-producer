package com.yuan.yuanaicodeproducer.model.dto.user;

import com.yuan.yuanaicodeproducer.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-19 17:26:20
 * @className UserQueryRequest
 * @description 用户查询请求
 */
// 自动生成 equals() 和 hashCode() 方法，并确保在比较子类对象时同时考虑父类的字段。
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "UserQueryRequest", description = "用户查询请求，支持按昵称、账号、角色等条件分页筛选")
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @Schema(description = "用户 ID")
    private Long id;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称（支持模糊匹配）")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "账号（支持模糊匹配）")
    private String userAccount;

    /**
     * 简介
     */
    @Schema(description = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @Schema(description = "用户角色：user/admin/ban")
    private String userRole;

    private static final long serialVersionUID = 3791271717373120793L;
}
