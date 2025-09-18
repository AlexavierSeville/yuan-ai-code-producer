package com.yuan.yuanaicodeproducer.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.yuan.yuanaicodeproducer.config.CosClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-01 17:00:27
 * @className CosManager
 * @description COS 对象存储管理器类
 * 不同规范下，Manager层的作用可能不同。比如《AlibabaJava开发手册》中的 Manager 层是指 通用业务处理层，它有如下特征:
 * 1.对第三方平台封装的层，预处理返回结果及转化异常信息(适配上层接口)
 * 2.对 Service 层通用能力的下沉，如缓存方案、中间件通用处理
 * 3.与 DAO 层交互，对多个 DAO 的组合复用
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CosManager {

    private final CosClientConfig cosClientConfig;

    private final COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到 COS 并返回访问 URL
     *
     * @param key  COS对象键（完整路径）
     * @param file 要上传的文件
     * @return 文件的访问URL，失败返回null
     */
    public String uploadFile(String key, File file) {
        // 上传文件
        PutObjectResult result = putObject(key, file);
        if (result != null) {
                    // 构建访问URL - 确保包含 https:// 协议头
        String url = String.format("https://%s%s", cosClientConfig.getHost(), key);
            log.info("文件上传COS成功: {} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传COS失败，返回结果为空");
            return null;
        }
    }
}
