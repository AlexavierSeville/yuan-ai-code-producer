package com.yuan.yuanaicodeproducer.config;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-09-12
 * @className ThreadPoolConfig
 * @description 线程池配置类，解决并发对话卡死问题
 */
@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {

    /**
     * 代码生成专用线程池
     * 解决并发对话时线程池资源竞争问题
     */
    @Bean("codeGenThreadPool")
    public ExecutorService codeGenThreadPool() {
        log.info("初始化代码生成线程池");
        return ExecutorBuilder.create()
                .setCorePoolSize(3)         // 核心线程数：3个
                .setMaxPoolSize(6)          // 最大线程数：6个
                .setWorkQueue(new LinkedBlockingQueue<>(20))  // 工作队列：20个
                .setThreadFactory(ThreadFactoryBuilder.create()
                        .setNamePrefix("CodeGen-")
                        .setDaemon(true)    // 设置为守护线程，JVM退出时自动结束
                        .build())
                .build();
    }

    /**
     * 图片收集专用线程池
     * 用于并发图片搜索任务
     */
    @Bean("imageCollectThreadPool")
    public ExecutorService imageCollectThreadPool() {
        log.info("初始化图片收集线程池");
        return ExecutorBuilder.create()
                .setCorePoolSize(2)         // 核心线程数：2个
                .setMaxPoolSize(4)          // 最大线程数：4个
                .setWorkQueue(new LinkedBlockingQueue<>(10))  // 工作队列：10个
                .setThreadFactory(ThreadFactoryBuilder.create()
                        .setNamePrefix("ImageCollect-")
                        .setDaemon(true)
                        .build())
                .build();
    }

    @Override
    public Executor getAsyncExecutor() {
        return codeGenThreadPool();
    }
}
