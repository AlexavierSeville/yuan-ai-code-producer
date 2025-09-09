package com.yuan.yuanaicodeproducer.langgraph4j.ai;

import com.yuan.yuanaicodeproducer.langgraph4j.model.QualityResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 16:37:02
 * @className CodeQualityCheckService
 * @description
 */
public interface CodeQualityCheckService {

    /**
     * 检查代码质量
     * AI 会分析代码并返回质量检查结果
     */
    @SystemMessage(fromResource = "prompt/code-quality-check-system-prompt.txt")
    QualityResult checkCodeQuality(@UserMessage String codeContent);
}

