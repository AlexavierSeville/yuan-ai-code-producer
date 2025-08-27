package com.yuan.yuanaicodeproducer.model.dto.app;

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
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    private static final long serialVersionUID = 3791271717373120793L;
}

