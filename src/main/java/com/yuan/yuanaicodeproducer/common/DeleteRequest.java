package com.yuan.yuanaicodeproducer.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 11:22:51
 * @className DeleteRequest
 * @description 删除请求包装类
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

