package com.yuan.yuanaicodeproducer.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 15:27:44
 * @className AiCodeGeneratorServiceFactory
 * @description AI服务创建工厂（工厂模式）
 */
@Configuration
@RequiredArgsConstructor
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;
    private final StreamingChatModel streamingChatModel;

    /**
     * 创建AI服务
     *
     * @return AI服务
     */
//    @Bean
//    public AiCodeGeneratorService createAiCodeGeneratorService() {
//        return AiServices.create(AiCodeGeneratorService.class, chatModel);
//    }
    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }

}
