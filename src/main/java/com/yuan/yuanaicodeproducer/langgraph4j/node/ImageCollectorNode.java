package com.yuan.yuanaicodeproducer.langgraph4j.node;

import com.yuan.yuanaicodeproducer.langgraph4j.ai.ImageCollectionPlanService;
import com.yuan.yuanaicodeproducer.langgraph4j.ai.ImageCollectionService;
import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageCollectionPlan;
import com.yuan.yuanaicodeproducer.langgraph4j.model.enums.ImageCategoryEnum;
import com.yuan.yuanaicodeproducer.langgraph4j.model.ImageResource;
import com.yuan.yuanaicodeproducer.langgraph4j.state.WorkflowContext;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.ImageSearchTool;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.LogoGeneratorTool;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.MermaidDiagramTool;
import com.yuan.yuanaicodeproducer.langgraph4j.tools.UndrawIllustrationTool;
import com.yuan.yuanaicodeproducer.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 图片收集节点 - 智能图片资源收集器
 * 
 * 这个节点是整个代码生成工作流中的第一个重要步骤，负责根据用户的原始需求，
 * 智能地收集各种类型的图片资源，为后续的代码生成提供丰富的视觉素材。
 * 
 * 主要功能：
 * 1. 使用AI分析用户需求，制定图片收集计划
 * 2. 并发执行多种类型的图片收集任务
 * 3. 收集结果并更新工作流上下文状态
 * 
 * 支持的图片类型：
 * - 内容图片：通过Pexels API搜索的真实照片
 * - 插画图片：通过Undraw API搜索的矢量插画
 * - 架构图：通过Mermaid代码生成的系统架构图
 * - Logo图片：通过AI生成的品牌标识
 * 
 * 技术特点：
 * - 使用CompletableFuture实现并发执行，提高收集效率
 * - 集成多种外部API和AI服务
 * - 异常处理确保工作流稳定性
 * 
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 10:31:53
 * @className ImageCollectorNode
 * @description 图片收集节点,使用AI进行工具调用，收集不同类型的图片
 */
@Slf4j
public class ImageCollectorNode {

    /**
     * 创建图片收集节点的异步操作
     * 
     * 这个方法返回一个AsyncNodeAction，它是LangGraph4j框架中用于定义异步节点操作的核心接口。
     * AsyncNodeAction封装了节点的执行逻辑，可以在工作流图中被调用。
     * 
     * @return AsyncNodeAction<MessagesState<String>> 异步节点操作，处理MessagesState类型的状态
     */
    public static AsyncNodeAction<MessagesState<String>> create() {
        // node_async是LangGraph4j提供的静态方法，用于创建异步节点操作
        // 它接受一个函数式接口，该接口定义了节点如何处理状态并返回新的状态
        return node_async(state -> {
            // ========== 第一步：获取工作流上下文 ==========
            // WorkflowContext是自定义的工作流上下文类，存储了整个工作流的状态信息
            // 包括用户原始提示词、当前执行步骤、收集的图片列表等
            WorkflowContext context = WorkflowContext.getContext(state);
            
            // 从上下文中获取用户原始输入的提示词
            // 这个提示词将用于AI分析需要收集哪些类型的图片
            String originalPrompt = context.getOriginalPrompt();
            
            // 初始化图片收集结果列表
            // ImageResource是图片资源的封装类，包含图片类别、描述、URL等信息
            List<ImageResource> collectedImages = new ArrayList<>();

            try {
                // ========== 第二步：AI制定图片收集计划 ==========
                // ImageCollectionPlanService是AI服务接口，使用LangChain4j框架
                // 它会根据用户的原始提示词，分析需要收集哪些类型的图片
                ImageCollectionPlanService planService = SpringContextUtil.getBean(ImageCollectionPlanService.class);
                
                // 调用AI服务，获取详细的图片收集计划
                // 返回的ImageCollectionPlan包含四种类型的任务列表：
                // - contentImageTasks: 内容图片搜索任务
                // - illustrationTasks: 插画图片搜索任务  
                // - diagramTasks: 架构图生成任务
                // - logoTasks: Logo生成任务
                ImageCollectionPlan plan = planService.planImageCollection(originalPrompt);
                log.info("获取到图片收集计划，开始并发执行");

                // ========== 第三步：并发执行各种图片收集任务 ==========
                // 使用CompletableFuture实现并发执行，提高收集效率
                // 每个Future代表一个异步任务，返回List<ImageResource>
                List<CompletableFuture<List<ImageResource>>> futures = new ArrayList<>();
                
                // 3.1 并发执行内容图片搜索任务
                // ImageSearchTool通过Pexels API搜索高质量的真实照片
                if (plan.getContentImageTasks() != null) {
                    ImageSearchTool imageSearchTool = SpringContextUtil.getBean(ImageSearchTool.class);
                    for (ImageCollectionPlan.ImageSearchTask task : plan.getContentImageTasks()) {
                        // 为每个搜索任务创建异步执行
                        futures.add(CompletableFuture.supplyAsync(() ->
                                imageSearchTool.searchContentImages(task.query())));
                    }
                }
                
                // 3.2 并发执行插画图片搜索任务
                // UndrawIllustrationTool通过Undraw API搜索矢量插画
                if (plan.getIllustrationTasks() != null) {
                    UndrawIllustrationTool illustrationTool = SpringContextUtil.getBean(UndrawIllustrationTool.class);
                    for (ImageCollectionPlan.IllustrationTask task : plan.getIllustrationTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() ->
                                illustrationTool.searchIllustrations(task.query())));
                    }
                }
                
                // 3.3 并发执行架构图生成任务
                // MermaidDiagramTool将Mermaid代码转换为SVG图片，用于展示系统架构
                if (plan.getDiagramTasks() != null) {
                    MermaidDiagramTool diagramTool = SpringContextUtil.getBean(MermaidDiagramTool.class);
                    for (ImageCollectionPlan.DiagramTask task : plan.getDiagramTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() ->
                                diagramTool.generateMermaidDiagram(task.mermaidCode(), task.description())));
                    }
                }
                
                // 3.4 并发执行Logo生成任务
                // LogoGeneratorTool使用阿里云DashScope AI服务生成Logo图片
                if (plan.getLogoTasks() != null) {
                    LogoGeneratorTool logoTool = SpringContextUtil.getBean(LogoGeneratorTool.class);
                    for (ImageCollectionPlan.LogoTask task : plan.getLogoTasks()) {
                        futures.add(CompletableFuture.supplyAsync(() ->
                                logoTool.generateLogos(task.description())));
                    }
                }

                // ========== 第四步：等待所有任务完成并收集结果 ==========
                // CompletableFuture.allOf()等待所有异步任务完成
                // 这里使用join()方法阻塞当前线程，直到所有任务完成
                CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0]));
                allTasks.join();
                
                // 遍历所有完成的Future，收集图片资源
                for (CompletableFuture<List<ImageResource>> future : futures) {
                    List<ImageResource> images = future.get(); // 获取任务结果
                    if (images != null) {
                        collectedImages.addAll(images); // 添加到总结果列表
                    }
                }
                log.info("并发图片收集完成，共收集到 {} 张图片", collectedImages.size());
                
            } catch (Exception e) {
                // 异常处理：如果图片收集过程中出现任何错误，记录日志但不中断工作流
                log.error("图片收集失败: {}", e.getMessage(), e);
            }
            
            // ========== 第五步：更新工作流上下文状态 ==========
            // 设置当前执行步骤为"图片收集"
            context.setCurrentStep("图片收集");
            
            // 将收集到的图片列表保存到上下文中
            // 后续的节点（如PromptEnhancerNode）会使用这些图片来增强提示词
            context.setImageList(collectedImages);
            
            // 返回更新后的上下文状态
            // WorkflowContext.saveContext()将上下文包装成Map格式，供LangGraph4j使用
            return WorkflowContext.saveContext(context);
        });
    }
}



