package com.yuan.yuanaicodeproducer.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yuan.yuanaicodeproducer.ai.tools.FileWriteTool;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import com.yuan.yuanaicodeproducer.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 15:27:44
 * @className AiCodeGeneratorServiceFactory
 * @description AI服务创建工厂（工厂模式）
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;
    // 普通模型
    private final StreamingChatModel openAiStreamingChatModel;
    // 推理模型
    private final StreamingChatModel reasoningStreamingChatModel;
    private final RedisChatMemoryStore redisChatMemoryStore;
    private final ChatHistoryService chatHistoryService;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     * 当我们多次对同一个appId的服务对话时，始终获取的时同一个aiService实例
     * 旧版，不带生成类型
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        // 先根据 appId 获取缓存中的对话历史，如果没有则创建新的
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.MULTI_FILE);
    }

    /**
     * 根据 appId 和代码生成类型获取服务（带缓存）
     * 新版，带生成类型
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }

    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        // 根据代码生成类型选择不同的模型配置
        return switch (codeGenType) {
            // Vue 项目生成使用推理模型
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    // 添加工具
                    .tools(new FileWriteTool())
                    // ai出现幻觉时做的处理
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                    ))
                    .build();
            // HTML 和多文件生成使用默认模型
            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "不支持的代码生成类型: " + codeGenType.getValue());
        };
    }



}
