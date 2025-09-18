package com.yuan.yuanaicodeproducer.model.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-08-27 15:19:36
 * @className AppDeployRequest
 * @description 应用部署请求
 */
@Data
@Schema(name = "AppDeployRequest", description = "应用部署请求，指定需要部署的应用 ID")
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    @Schema(description = "应用 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long appId;

    private static final long serialVersionUID = 3791271717373120793L;
}

