package com.yuan.yuanaicodeproducer.ratelimit.enums;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-10 16:07:13
 * @className RateLimitType
 * @description
 */
public enum RateLimitType {

    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}
