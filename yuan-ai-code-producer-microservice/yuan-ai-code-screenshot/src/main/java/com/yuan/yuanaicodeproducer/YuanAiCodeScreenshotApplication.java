package com.yuan.yuanaicodeproducer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-19 09:14:41
 * @className YuanAiCodeScreenShotApplication
 * @description
 */
@EnableDubbo
@SpringBootApplication
public class YuanAiCodeScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuanAiCodeScreenshotApplication.class, args);
    }
}

