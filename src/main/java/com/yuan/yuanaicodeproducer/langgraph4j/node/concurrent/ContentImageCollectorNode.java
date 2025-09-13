package com.yuan.yuanaicodeproducer.langgraph4j.node.concurrent;

import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageCollectionPlan;
import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageResource;
import com.yuan.yuanaicodeproducer.langgraph4j.state.WorkflowContext;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.ImageSearchTool;
import com.yuan.yuanaicodeproducer.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 17:21:32
 * @className ContentImageCollectorNode
 * @description
 */
@Slf4j
public class ContentImageCollectorNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            List<ImageResource> contentImages = new ArrayList<>();
            try {
                ImageCollectionPlan plan = context.getImageCollectionPlan();
                if (plan != null && plan.getContentImageTasks() != null) {
                    ImageSearchTool imageSearchTool = SpringContextUtil.getBean(ImageSearchTool.class);
                    log.info("开始并发收集内容图片，任务数: {}", plan.getContentImageTasks().size());
                    
                    // 创建并发任务
                    List<CompletableFuture<List<ImageResource>>> futures = new ArrayList<>();
                    for (ImageCollectionPlan.ImageSearchTask task : plan.getContentImageTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() -> 
                            imageSearchTool.searchContentImages(task.query())));
                    }
                    
                    // 等待所有任务完成
                    CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]));
                    allTasks.join();
                    
                    // 收集结果
                    for (CompletableFuture<List<ImageResource>> future : futures) {
                        List<ImageResource> images = future.join();
                        if (images != null) {
                            contentImages.addAll(images);
                        }
                    }
                    log.info("内容图片并发收集完成，共收集到 {} 张图片", contentImages.size());
                }
            } catch (Exception e) {
                log.error("内容图片收集失败: {}", e.getMessage(), e);
            }
            // 将收集到的图片存储到上下文的中间字段中
            context.setContentImages(contentImages);
            context.setCurrentStep("内容图片收集");
            return WorkflowContext.saveContext(context);
        });
    }
}

