package com.yuan.yuanaicodeproducer.langgraph4j;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.langgraph4j.model.QualityResult;
import com.yuan.yuanaicodeproducer.langgraph4j.node.*;
import com.yuan.yuanaicodeproducer.langgraph4j.node.concurrent.*;
import com.yuan.yuanaicodeproducer.langgraph4j.state.WorkflowContext;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 17:27:58
 * @className CodeGenConcurrentWorkflow
 * @description 并发工作流
 */
@Slf4j
public class CodeGenConcurrentWorkflow {

    /**
     * 创建并发工作流
     */
    public CompiledGraph<MessagesState<String>> createWorkflow() {
        try {
            return new MessagesStateGraph<String>()
                    // 添加节点
                    .addNode("image_plan", ImagePlanNode.create())
                    .addNode("prompt_enhancer", PromptEnhancerNode.create())
                    .addNode("router", RouterNode.create())
                    .addNode("code_generator", CodeGeneratorNode.create())
                    .addNode("code_quality_check", CodeQualityCheckNode.create())
                    .addNode("project_builder", ProjectBuilderNode.create())

                    // 添加并发图片收集节点
                    .addNode("content_image_collector", ContentImageCollectorNode.create())
                    .addNode("illustration_collector", IllustrationCollectorNode.create())
                    .addNode("diagram_collector", DiagramCollectorNode.create())
                    .addNode("logo_collector", LogoCollectorNode.create())
                    .addNode("image_aggregator", ImageAggregatorNode.create())

                    // 添加边
                    .addEdge(START, "image_plan")

                    // 并发分支：从计划节点分发到各个收集节点
                    .addEdge("image_plan", "content_image_collector")
                    .addEdge("image_plan", "illustration_collector")
                    .addEdge("image_plan", "diagram_collector")
                    .addEdge("image_plan", "logo_collector")

                    // 汇聚：所有收集节点都汇聚到聚合器
                    .addEdge("content_image_collector", "image_aggregator")
                    .addEdge("illustration_collector", "image_aggregator")
                    .addEdge("diagram_collector", "image_aggregator")
                    .addEdge("logo_collector", "image_aggregator")

                    // 继续串行流程
                    .addEdge("image_aggregator", "prompt_enhancer")
                    .addEdge("prompt_enhancer", "router")
                    .addEdge("router", "code_generator")
                    .addEdge("code_generator", "code_quality_check")

                    // 质检条件边
                    .addConditionalEdges("code_quality_check",
                            edge_async(this::routeAfterQualityCheck),
                            Map.of(
                                    "build", "project_builder",
                                    "skip_build", END,
                                    "fail", "code_generator"
                            ))
                    .addEdge("project_builder", END)
                    .compile();
        } catch (GraphStateException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "并发工作流创建失败");
        }
    }

    /**
     * 执行并发工作流
     */
    public WorkflowContext executeWorkflow(String originalPrompt) {
        CompiledGraph<MessagesState<String>> workflow = createWorkflow();
        WorkflowContext initialContext = WorkflowContext.builder()
                .originalPrompt(originalPrompt)
                .currentStep("初始化")
                .build();
        GraphRepresentation graph = workflow.getGraph(GraphRepresentation.Type.MERMAID);
        log.info("并发工作流图:\n{}", graph.content());
        log.info("开始执行并发代码生成工作流");
        WorkflowContext finalContext = null;
        int stepCounter = 1;
        /*
         * ========== 配置并发执行，否则仍然是串行 ==========
         * 
         * 这部分代码是并发工作流的核心配置，用于将原本串行执行的图片收集任务
         * 改为并发执行，大大提高执行效率。
         * 
         * 并发执行原理：
         * 1. 创建线程池：管理并发线程的生命周期
         * 2. 配置节点执行器：指定哪些节点使用并发执行
         * 3. 执行工作流：使用配置的并发设置运行工作流
         * 
         * 并发 vs 串行对比：
         * - 串行执行：image_plan → content_collector → illustration_collector → diagram_collector → logo_collector
         *   总时间 = 各任务时间之和
         * - 并发执行：image_plan → [content_collector, illustration_collector, diagram_collector, logo_collector]
         *   总时间 ≈ 最慢任务的时间
         */
        
        // 创建线程池 - 这是并发执行的基础设施
        ExecutorService pool = ExecutorBuilder.create()
                .setCorePoolSize(10)        // 核心线程数：始终保持10个活跃线程
                .setMaxPoolSize(20)         // 最大线程数：最多可创建20个线程
                .setWorkQueue(new LinkedBlockingQueue<>(100))  // 工作队列：最多缓存100个待执行任务
                .setThreadFactory(ThreadFactoryBuilder.create()
                        .setNamePrefix("Parallel-Image-Collect")  // 线程名前缀：便于调试和监控
                        .build())
                .build();
        
        // 配置工作流的并发执行策略
        // RunnableConfig 告诉 LangGraph4j 框架哪些节点需要使用并发执行
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .addParallelNodeExecutor("image_plan", pool)  // 为 image_plan 节点配置线程池
                .build();
        
        // 使用并发配置执行工作流
        // 这里的工作流执行会使用上面配置的线程池来实现并发
        for (NodeOutput<MessagesState<String>> step : workflow.stream(
                Map.of(WorkflowContext.WORKFLOW_CONTEXT_KEY, initialContext),
                runnableConfig)) {
            // 空的循环体，只是为了触发并发执行
            // 实际的并发逻辑在 LangGraph4j 框架内部处理
        }

        for (NodeOutput<MessagesState<String>> step : workflow.stream(
                Map.of(WorkflowContext.WORKFLOW_CONTEXT_KEY, initialContext)
        )) {
            log.info("--- 第 {} 步完成 ---", stepCounter);
            WorkflowContext currentContext = WorkflowContext.getContext(step.state());
            if (currentContext != null) {
                finalContext = currentContext;
                log.info("当前步骤上下文: {}", currentContext);
            }
            stepCounter++;
        }
        log.info("并发代码生成工作流执行完成！");
        return finalContext;
    }

    /**
     * 路由函数：根据质检结果决定下一步
     */
    private String routeAfterQualityCheck(MessagesState<String> state) {
        WorkflowContext context = WorkflowContext.getContext(state);
        QualityResult qualityResult = context.getQualityResult();

        if (qualityResult == null || !qualityResult.getIsValid()) {
            log.error("代码质检失败，需要重新生成代码");
            return "fail";
        }
        log.info("代码质检通过，继续后续流程");
        CodeGenTypeEnum generationType = context.getGenerationType();
        if (generationType == CodeGenTypeEnum.VUE_PROJECT) {
            return "build";
        } else {
            return "skip_build";
        }
    }
}

