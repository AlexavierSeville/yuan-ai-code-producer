package com.yuan.yuanaicodeproducer;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-18 17:05:01
 * @className YuanAicodeAppApplication
 * @description 启动类
 */
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.yuan.yuanaicodeproducer.mapper")
@EnableCaching
public class YuanAiCodeAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuanAiCodeAppApplication.class, args);
    }
}

