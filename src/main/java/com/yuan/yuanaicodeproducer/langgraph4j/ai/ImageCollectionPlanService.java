package com.yuan.yuanaicodeproducer.langgraph4j.ai;

import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageCollectionPlan;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 17:04:46
 * @className ImageCollectionPlanService
 * @description
 */
public interface ImageCollectionPlanService {

    /**
     * 根据用户提示词分析需要收集的图片类型和参数
     */
    @SystemMessage(fromResource = "prompt/image-collection-plan-system-prompt.txt")
    ImageCollectionPlan planImageCollection(@UserMessage String userPrompt);
}

