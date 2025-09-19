package com.yuan.yuanaicodeproducer.ai.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.yuan.yuanaicodeproducer.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-03 09:12:25
 * @className FileReadTool
 * @description 文件读取工具 支持 AI 通过工具调用的方式读取文件内容
 */
@Slf4j
@Component
public class FileReadTool extends BaseTool{

    @Tool("读取指定路径的文件内容")
    public String readFile(
            @P("文件的相对路径")
            String relativeFilePath,
            @ToolMemoryId Long appId
    ) {
        try {
            // 确保始终在项目目录内操作，避免访问系统文件
            String projectDirName = "vue_project_" + appId;
            Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
            Path path;
            
            if (StrUtil.isBlank(relativeFilePath)) {
                return "错误：文件路径不能为空";
            }
            
            Path relativePath = Paths.get(relativeFilePath);
            if (relativePath.isAbsolute()) {
                // 如果是绝对路径，检查是否在项目目录内
                if (!relativePath.startsWith(projectRoot)) {
                    return "错误：文件路径必须在项目目录内 - " + relativeFilePath;
                }
                path = relativePath;
            } else {
                // 相对路径，与项目根目录组合
                path = projectRoot.resolve(relativePath).normalize();
                // 再次检查是否在项目目录内（防止路径遍历攻击）
                if (!path.startsWith(projectRoot)) {
                    return "错误：文件路径必须在项目目录内 - " + relativeFilePath;
                }
            }
            
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return "错误：文件不存在或不是文件 - " + relativeFilePath;
            }
            return Files.readString(path);
        } catch (IOException e) {
            String errorMessage = "读取文件失败: " + relativeFilePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    @Override
    public String getToolName() {
        return "readFile";
    }

    @Override
    public String getDisplayName() {
        return "读取文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        return String.format("[工具调用] %s %s", getDisplayName(), relativeFilePath);
    }
}

