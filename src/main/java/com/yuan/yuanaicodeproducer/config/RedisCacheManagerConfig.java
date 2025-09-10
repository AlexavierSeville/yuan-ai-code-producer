package com.yuan.yuanaicodeproducer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-10 15:03:58
 * @className RedisCacheManagerConfig
 * @description Redis 缓存管理器配置
 * 
 * 功能说明：
 * 1. 配置Spring Cache使用Redis作为缓存存储
 * 2. 设置统一的缓存序列化策略（Key用String，Value用JSON）
 * 3. 配置不同缓存空间的过期时间策略
 * 4. 支持Java8时间类型的序列化和反序列化
 * 
 * 缓存策略：
 * - 默认缓存：30分钟过期
 * - 精选应用缓存：5分钟过期（数据更新频率适中）
 * - 禁用null值缓存：避免缓存无意义的空值
 */
@Configuration
@RequiredArgsConstructor
public class RedisCacheManagerConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 配置Redis缓存管理器
     * 
     * 配置说明：
     * 1. 序列化策略：Key使用String序列化，Value使用JSON序列化
     * 2. 过期策略：支持全局默认过期时间和特定缓存空间过期时间
     * 3. 空值处理：禁用null值缓存，避免缓存无意义数据
     * 4. 时间支持：支持Java8时间类型的序列化
     * 
     * @return CacheManager Spring缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        // 配置ObjectMapper支持Java8时间类型序列化
        // 解决LocalDateTime、LocalDate等时间类型在Redis中的存储问题
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 启用默认类型信息（针对复杂对象序列化）

        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认缓存30分钟过期
                .disableCachingNullValues() // 禁用null值缓存，避免缓存无意义数据
                // 缓存键序列化：使用String序列化器，便于在Redis中查看和管理
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()));
//                // 缓存值序列化：使用JSON序列化器，支持复杂对象的存储和读取
//                // 注意：开启后需要给序列化增加默认类型配置，否则无法反序列化
//                .serializeValuesWith(RedisSerializationContext.SerializationPair
//                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        // 构建Redis缓存管理器
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig) // 设置默认缓存配置
                // 针对精选应用列表缓存配置特殊过期时间
                // 精选应用数据相对稳定，5分钟过期平衡性能与实时性
                .withCacheConfiguration("good_app_page",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}

