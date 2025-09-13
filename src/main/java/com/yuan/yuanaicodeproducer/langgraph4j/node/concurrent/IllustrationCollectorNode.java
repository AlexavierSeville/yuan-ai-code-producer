package com.yuan.yuanaicodeproducer.langgraph4j.node.concurrent;

import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageCollectionPlan;
import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageResource;
import com.yuan.yuanaicodeproducer.langgraph4j.state.WorkflowContext;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.UndrawIllustrationTool;
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
 * @date 2025-09-09 17:22:12
 * @className IllustrationCollectorNode
 * @description
 */
@Slf4j
public class IllustrationCollectorNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            List<ImageResource> illustrations = new ArrayList<>();
            try {
                ImageCollectionPlan plan = context.getImageCollectionPlan();
                if (plan != null && plan.getIllustrationTasks() != null) {
                    UndrawIllustrationTool illustrationTool = SpringContextUtil.getBean(UndrawIllustrationTool.class);
                    log.info("开始并发收集插画图片，任务数: {}", plan.getIllustrationTasks().size());
                    
                    // 创建并发任务
                    List<CompletableFuture<List<ImageResource>>> futures = new ArrayList<>();
                    for (ImageCollectionPlan.IllustrationTask task : plan.getIllustrationTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() -> 
                            illustrationTool.searchIllustrations(task.query())));
                    }
                    
                    // 等待所有任务完成
                    CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]));
                    allTasks.join();
                    
                    // 收集结果
                    for (CompletableFuture<List<ImageResource>> future : futures) {
                        List<ImageResource> images = future.join();
                        if (images != null) {
                            illustrations.addAll(images);
                        }
                    }
                    log.info("插画图片并发收集完成，共收集到 {} 张图片", illustrations.size());
                }
            } catch (Exception e) {
                log.error("插画图片收集失败: {}", e.getMessage(), e);
            }
            context.setIllustrations(illustrations);
            context.setCurrentStep("插画图片收集");
            return WorkflowContext.saveContext(context);
        });
    }
}

