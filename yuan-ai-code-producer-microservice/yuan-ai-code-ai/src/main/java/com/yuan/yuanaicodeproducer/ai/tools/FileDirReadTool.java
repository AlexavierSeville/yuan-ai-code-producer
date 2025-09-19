package com.yuan.yuanaicodeproducer.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.yuan.yuanaicodeproducer.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-03 09:09:40
 * @className FileDirReadTool
 * @description 文件目录读取工具 使用 Hutool 简化文件操作
 */
@Slf4j
@Component
public class FileDirReadTool extends BaseTool{

    /**
     * 需要忽略的文件和目录
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build", ".DS_Store",
            ".env", "target", ".mvn", ".idea", ".vscode", "coverage",
            "System Volume Information", "System32", "Windows", "Program Files",
            "Program Files (x86)", "AppData", "Local Settings", "Temp",
            "Temporary Internet Files", "Recycle.Bin", "$Recycle.Bin"
    );

    /**
     * 需要忽略的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log", ".tmp", ".cache", ".lock"
    );

    @Tool("读取目录结构，获取指定目录下的所有文件和子目录信息")
    public String readDir(
            @P("目录的相对路径，为空则读取整个项目结构")
            String relativeDirPath,
            @ToolMemoryId Long appId
    ) {
        try {
            // 确保始终在项目目录内操作，避免访问系统目录
            String projectDirName = "vue_project_" + appId;
            Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
            Path path;
            
            if (StrUtil.isBlank(relativeDirPath)) {
                // 如果路径为空，直接使用项目根目录
                path = projectRoot;
            } else {
                // 解析相对路径，确保在项目目录内
                Path relativePath = Paths.get(relativeDirPath);
                if (relativePath.isAbsolute()) {
                    // 如果是绝对路径，检查是否在项目目录内
                    if (!relativePath.startsWith(projectRoot)) {
                        return "错误：路径必须在项目目录内 - " + relativeDirPath;
                    }
                    path = relativePath;
                } else {
                    // 相对路径，与项目根目录组合
                    path = projectRoot.resolve(relativePath).normalize();
                    // 再次检查是否在项目目录内（防止路径遍历攻击）
                    if (!path.startsWith(projectRoot)) {
                        return "错误：路径必须在项目目录内 - " + relativeDirPath;
                    }
                }
            }
            
            File targetDir = path.toFile();
            if (!targetDir.exists() || !targetDir.isDirectory()) {
                return "错误：目录不存在或不是目录 - " + relativeDirPath;
            }
            
            StringBuilder structure = new StringBuilder();
            structure.append("项目目录结构:\n");
            
            // 使用 Hutool 递归获取所有文件，添加异常处理
            List<File> allFiles;
            try {
                allFiles = FileUtil.loopFiles(targetDir, file -> !shouldIgnore(file.getName()));
            } catch (Exception e) {
                log.warn("读取目录时遇到权限问题，尝试只读取直接子目录: {}", e.getMessage());
                // 如果递归读取失败，尝试只读取直接子目录
                allFiles = FileUtil.loopFiles(targetDir, 1, file -> !shouldIgnore(file.getName()));
            }
            
            // 按路径深度和名称排序显示
            allFiles.stream()
                    .sorted((f1, f2) -> {
                        int depth1 = getRelativeDepth(targetDir, f1);
                        int depth2 = getRelativeDepth(targetDir, f2);
                        if (depth1 != depth2) {
                            return Integer.compare(depth1, depth2);
                        }
                        return f1.getPath().compareTo(f2.getPath());
                    })
                    .forEach(file -> {
                        int depth = getRelativeDepth(targetDir, file);
                        String indent = "  ".repeat(depth);
                        structure.append(indent).append(file.getName()).append("\n");
                    });
            return structure.toString();

        } catch (Exception e) {
            String errorMessage = "读取目录结构失败: " + relativeDirPath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 计算文件相对于根目录的深度
     */
    private int getRelativeDepth(File root, File file) {
        Path rootPath = root.toPath();
        Path filePath = file.toPath();
        return rootPath.relativize(filePath).getNameCount() - 1;
    }

    /**
     * 判断是否应该忽略该文件或目录
     */
    private boolean shouldIgnore(String fileName) {
        // 检查是否在忽略名称列表中
        if (IGNORED_NAMES.contains(fileName)) {
            return true;
        }

        // 检查文件扩展名
        return IGNORED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    @Override
    public String getToolName() {
        return "readDir";
    }

    @Override
    public String getDisplayName() {
        return "读取目录";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        if (StrUtil.isEmpty(relativeFilePath)){
            relativeFilePath = "根目录";
        }
        return String.format("[工具调用] %s %s",getDisplayName(), relativeFilePath);
    }
}

