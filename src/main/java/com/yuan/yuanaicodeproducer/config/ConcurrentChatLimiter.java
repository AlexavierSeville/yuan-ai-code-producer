package com.yuan.yuanaicodeproducer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-09-12
 * @className ConcurrentChatLimiter
 * @description 并发对话限制器，防止同时开启过多对话导致系统卡死
 */
@Slf4j
@Component
public class ConcurrentChatLimiter {
    
    /**
     * 最大并发对话数
     */
    private static final int MAX_CONCURRENT_CHATS = 3;
    
    /**
     * 信号量控制并发数量
     */
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_CHATS);
    
    /**
     * 当前活跃对话数
     */
    private final AtomicInteger activeChats = new AtomicInteger(0);
    
    /**
     * 尝试获取对话许可
     * 
     * @return true表示获取成功，false表示系统繁忙
     */
    public boolean tryAcquire() {
        boolean acquired = semaphore.tryAcquire();
        if (acquired) {
            int current = activeChats.incrementAndGet();
            log.info("获取对话许可成功，当前活跃对话数: {}/{}", current, MAX_CONCURRENT_CHATS);
        } else {
            log.warn("系统繁忙，当前活跃对话数已达上限: {}/{}", activeChats.get(), MAX_CONCURRENT_CHATS);
        }
        return acquired;
    }
    
    /**
     * 释放对话许可
     */
    public void release() {
        semaphore.release();
        int current = activeChats.decrementAndGet();
        log.info("释放对话许可，当前活跃对话数: {}/{}", current, MAX_CONCURRENT_CHATS);
    }
    
    /**
     * 获取当前活跃对话数
     */
    public int getActiveChats() {
        return activeChats.get();
    }
    
    /**
     * 获取可用许可数
     */
    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }
}
