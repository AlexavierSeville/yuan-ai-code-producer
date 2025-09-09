package com.yuan.yuanaicodeproducer.langgraph4j.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-09 17:02:20
 * @className ImageCollectionPlan
 * @description
 */
@Data
public class ImageCollectionPlan implements Serializable {

    /**
     * 内容图片搜索任务列表
     */
    private List<ImageSearchTask> contentImageTasks;

    /**
     * 插画图片搜索任务列表
     */
    private List<IllustrationTask> illustrationTasks;

    /**
     * 架构图生成任务列表
     */
    private List<DiagramTask> diagramTasks;

    /**
     * Logo生成任务列表
     */
    private List<LogoTask> logoTasks;

    /**
     * 内容图片搜索任务，record是一种用于建模不可变数据的特殊类。它通过在 Java 14 中作为预览特性引入，并在 Java 16 中成为正式标准，
     * 旨在简化那些主要目的是保存数据的类的编写，能自动生成许多样板代码。
     * 对应 ImageSearchTool.searchContentImages(String query)
     */
    public record ImageSearchTask(String query) implements Serializable {}

    /**
     * 插画图片搜索任务
     * 对应 UndrawIllustrationTool.searchIllustrations(String query)
     */
    public record IllustrationTask(String query) implements Serializable {}

    /**
     * 架构图生成任务
     * 对应 MermaidDiagramTool.generateMermaidDiagram(String mermaidCode, String description)
     */
    public record DiagramTask(String mermaidCode, String description) implements Serializable {}

    /**
     * Logo生成任务
     * 对应 LogoGeneratorTool.generateLogos(String description)
     */
    public record LogoTask(String description) implements Serializable {}
}

