package com.yuan.yuanaicodeproducer.monitor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-15 17:06:08
 * @className MonitorContextHolder
 * @description 提供 ThreadLocal 的读、写、清除方法
 */
@Slf4j
public class MonitorContextHolder {

    private static final ThreadLocal<MonitorContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置监控上下文
     */
    public static void setContext(MonitorContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取当前监控上下文
     */
    public static MonitorContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除监控上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}

