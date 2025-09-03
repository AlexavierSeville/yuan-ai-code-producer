package com.yuan.yuanaicodeproducer.ai;

import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-02 09:49:50
 * @className AiCodeGenTypeRoutingService
 * @description AI代码生成类型智能路由服务，使用结构化输出直接返回枚举
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户需求智能选择代码生成类型
     *
     * @param userPrompt 用户输入的需求描述
     * @return 推荐的代码生成类型
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}

