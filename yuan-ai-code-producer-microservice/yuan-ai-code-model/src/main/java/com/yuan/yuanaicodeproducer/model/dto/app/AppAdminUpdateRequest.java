package com.yuan.yuanaicodeproducer.model.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-26 15:09:38
 * @className AppAdminUpdateRequest
 * @description 更新应用请求
 */
@Data
@Schema(name = "AppAdminUpdateRequest", description = "管理员更新应用请求，支持更新名称、封面、优先级等")
public class AppAdminUpdateRequest implements Serializable {

    /**
     * id
     */
    @Schema(description = "应用 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用封面
     */
    @Schema(description = "应用封面 URL")
    private String cover;

    /**
     * 优先级
     */
    @Schema(description = "应用优先级，用于精选排序等场景")
    private Integer priority;

    private static final long serialVersionUID = 3791271717373120793L;
}

