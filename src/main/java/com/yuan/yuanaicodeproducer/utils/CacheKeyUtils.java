package com.yuan.yuanaicodeproducer.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-10 14:51:08
 * @className CacheKeyUtils
 * @description 缓存键生成工具类
 * 
 * 功能说明：
 * 1. 为Spring Cache提供统一的缓存键生成策略
 * 2. 将复杂对象转换为唯一的字符串键，避免缓存键冲突
 * 3. 使用JSON序列化 + MD5哈希确保键的唯一性和一致性
 * 4. 支持任意Java对象作为缓存键的输入
 */
public class CacheKeyUtils {

    /**
     * 根据对象生成缓存键（JSON序列化 + MD5哈希）
     * 
     * 生成流程：
     * 1. 将输入对象转换为JSON字符串
     * 2. 对JSON字符串计算MD5哈希值
     * 3. 返回32位十六进制字符串作为缓存键
     * 
     * 优势：
     * - 唯一性：相同对象内容生成相同键，不同对象内容生成不同键
     * - 一致性：对象字段顺序不影响键的生成结果
     * - 安全性：MD5哈希避免键中包含敏感信息
     * - 长度固定：32位十六进制字符串，便于存储和比较
     * 
     * 使用场景：
     * - Spring Cache的@Cacheable注解中的key属性
     * - 复杂查询条件的缓存键生成
     * - 分页查询参数的缓存键生成
     *
     * @param obj 要生成缓存键的对象（如AppQueryRequest、UserQueryRequest等）
     * @return MD5哈希后的32位十六进制缓存键
     * 
     * @example
     * AppQueryRequest request = new AppQueryRequest();
     * request.setPageNum(1);
     * request.setPageSize(10);
     * String cacheKey = CacheKeyUtils.generateKey(request);
     * // 结果：a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
     */
    public static String generateKey(Object obj) {
        // 处理null对象，避免空指针异常
        if (obj == null) {
            return DigestUtil.md5Hex("null");
        }
        
        // 步骤1：将对象序列化为JSON字符串
        // 使用Hutool的JSONUtil，支持复杂对象和嵌套结构
        String jsonStr = JSONUtil.toJsonStr(obj);
        
        // 步骤2：对JSON字符串计算MD5哈希值
        // MD5生成32位十六进制字符串，确保键的唯一性和固定长度
        return DigestUtil.md5Hex(jsonStr);
    }
}

