package com.yuan.yuanaicodeproducer.model.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-26 10:57:56
 * @className AppUpdateRequest
 * @description 更新应用请求
 */
@Data
@Schema(name = "AppUpdateRequest", description = "应用更新请求，允许更新应用名称等基本信息")
public class AppUpdateRequest implements Serializable {

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

    private static final long serialVersionUID = 3791271717373120793L;
}

