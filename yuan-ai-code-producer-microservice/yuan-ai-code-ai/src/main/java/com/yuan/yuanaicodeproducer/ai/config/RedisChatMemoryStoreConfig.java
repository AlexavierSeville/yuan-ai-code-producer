package com.yuan.yuanaicodeproducer.ai.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-08-28 16:20:18
 * @className RedisChatMemoryStoreConfig
 * @description langchain4j为redis提供的存储bean,用于持久化用户记忆
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisChatMemoryStoreConfig {
    private String host;
    private int port;
    private String password;
    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        return RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .user("default")
                .password(password)
                .ttl(ttl)
                .build();
    }
}
