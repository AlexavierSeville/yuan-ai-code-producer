package com.yuan.yuanaicodeproducer.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用 实体类。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app")
@Schema(name = "App", description = "应用实体，描述应用的基础信息与部署状态")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 3791271717373120793L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Schema(description = "主键 ID")
    private Long id;

    /**
     * 应用名称
     */
    @Column("appName")
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用封面
     */
    @Schema(description = "应用封面 URL")
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    @Column("initPrompt")
    @Schema(description = "初始化 Prompt")
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    @Column("codeGenType")
    @Schema(description = "代码生成类型（枚举）")
    private String codeGenType;

    /**
     * 部署标识
     */
    @Column("deployKey")
    @Schema(description = "部署标识，用于预览与访问")
    private String deployKey;

    /**
     * 部署时间
     */
    @Column("deployedTime")
    @Schema(description = "最近部署时间")
    private LocalDateTime deployedTime;

    /**
     * 优先级
     */
    @Schema(description = "优先级（用于精选排序等）")
    private Integer priority;

    /**
     * 创建用户id
     */
    @Column("userId")
    @Schema(description = "创建者用户 ID")
    private Long userId;

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
