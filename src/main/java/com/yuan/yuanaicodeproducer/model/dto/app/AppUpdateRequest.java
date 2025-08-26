package com.yuan.yuanaicodeproducer.model.dto.app;

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
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    private static final long serialVersionUID = 3791271717373120793L;
}

