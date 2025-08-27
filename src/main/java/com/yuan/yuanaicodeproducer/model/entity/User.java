package com.yuan.yuanaicodeproducer.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户 实体类。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
@Schema(name = "User", description = "用户实体，存储账号、昵称、角色等基础信息")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Schema(description = "主键 ID")
    private Long id;

    /**
     * 账号
     */
    @Column("userAccount")
    @Schema(description = "账号（唯一）")
    private String userAccount;

    /**
     * 密码
     */
    @Column("userPassword")
    @Schema(description = "密码（加密存储）")
    private String userPassword;

    /**
     * 用户昵称
     */
    @Column("userName")
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @Column("userAvatar")
    @Schema(description = "用户头像 URL")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Column("userProfile")
    @Schema(description = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @Column("userRole")
    @Schema(description = "用户角色：user/admin")
    private String userRole;

    /**
     * 编辑时间
     */
    @Column("editTime")
    @Schema(description = "编辑时间")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @Column("createTime")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    @Schema(description = "逻辑删除标记：0-未删，1-已删")
    private Integer isDelete;

}
