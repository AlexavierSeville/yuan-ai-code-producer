package com.yuan.yuanaicodeproducer.model.dto.app;

import com.yuan.yuanaicodeproducer.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-26 11:35:04
 * @className AppQueryRequest
 * @description 查询请求类，主要定义了可作为查询条件的字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "AppQueryRequest", description = "应用查询请求，支持按多条件分页筛选")
public class AppQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @Schema(description = "应用 ID")
    private Long id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称（支持模糊匹配）")
    private String appName;

    /**
     * 应用封面
     */
    @Schema(description = "应用封面 URL")
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    @Schema(description = "初始化 Prompt（支持模糊匹配）")
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    @Schema(description = "代码生成类型（枚举值）")
    private String codeGenType;

    /**
     * 部署标识
     */
    @Schema(description = "部署标识，用于静态资源访问")
    private String deployKey;

    /**
     * 优先级
     */
    @Schema(description = "优先级（数值越大越靠前）")
    private Integer priority;

    /**
     * 创建用户id
     */
    @Schema(description = "创建者用户 ID")
    private Long userId;

    private static final long serialVersionUID = 3791271717373120793L;
}

