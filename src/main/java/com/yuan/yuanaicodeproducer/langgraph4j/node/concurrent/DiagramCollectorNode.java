package com.yuan.yuanaicodeproducer.langgraph4j.node.concurrent;

import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageCollectionPlan;
import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageResource;
import com.yuan.yuanaicodeproducer.langgraph4j.state.WorkflowContext;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.MermaidDiagramTool;
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
 * @date 2025-09-09 17:22:50
 * @className DiagramCollectorNode
 * @description
 */
@Slf4j
public class DiagramCollectorNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            List<ImageResource> diagrams = new ArrayList<>();
            try {
                ImageCollectionPlan plan = context.getImageCollectionPlan();
                if (plan != null && plan.getDiagramTasks() != null) {
                    MermaidDiagramTool diagramTool = SpringContextUtil.getBean(MermaidDiagramTool.class);
                    log.info("开始并发生成架构图，任务数: {}", plan.getDiagramTasks().size());
                    
                    // 创建并发任务
                    List<CompletableFuture<List<ImageResource>>> futures = new ArrayList<>();
                    for (ImageCollectionPlan.DiagramTask task : plan.getDiagramTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() -> 
                            diagramTool.generateMermaidDiagram(task.mermaidCode(), task.description())));
                    }
                    
                    // 等待所有任务完成
                    CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]));
                    allTasks.join();
                    
                    // 收集结果
                    for (CompletableFuture<List<ImageResource>> future : futures) {
                        List<ImageResource> images = future.join();
                        if (images != null) {
                            diagrams.addAll(images);
                        }
                    }
                    log.info("架构图并发生成完成，共生成 {} 张图片", diagrams.size());
                }
            } catch (Exception e) {
                log.error("架构图生成失败: {}", e.getMessage(), e);
            }
            context.setDiagrams(diagrams);
            context.setCurrentStep("架构图生成");
            return WorkflowContext.saveContext(context);
        });
    }
}

