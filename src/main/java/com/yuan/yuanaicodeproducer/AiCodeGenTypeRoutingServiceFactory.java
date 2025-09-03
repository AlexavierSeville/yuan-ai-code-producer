package com.yuan.yuanaicodeproducer;

import com.yuan.yuanaicodeproducer.ai.AiCodeGenTypeRoutingService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-02 09:51:42
 * @className AiCodeGenTypeRoutingServiceFactory
 * @description AI代码生成类型路由服务工厂
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiCodeGenTypeRoutingServiceFactory {

    private final ChatModel chatModel;

    /**
     * 创建AI代码生成类型路由服务实例
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}

