package com.yuan.yuanaicodeproducer.common;

import lombok.Data;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 11:12:03
 * @className PageRequest
 * @description 请求包装类
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

