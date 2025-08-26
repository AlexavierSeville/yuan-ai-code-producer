package com.yuan.yuanaicodeproducer.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yuan.yuanaicodeproducer.ai.model.HtmlCodeResult;
import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 16:18:30
 * @className CodeFileSaver；已弃用，新方法定义于saver包内
 * @description 把生成的代码文件保存到本地服务器
 */
@Deprecated
public class CodeFileSaver {

    // 文件保存根目录，获取当前工作目录并拼接上 "/tmp/code_output"
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存 HtmlCodeResult 的方法
     *
     * @param result HtmlCodeResult 对象，包含生成的 HTML 代码
     * @return 返回保存 HTML 代码的文件夹
     */
    public static File saveHtmlCodeResult(HtmlCodeResult result) {
        // 构建唯一的目录路径，依据 HTML 代码的业务类型
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        // 将 HTML 代码写入到该目录下的 "index.html" 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        // 返回保存的目录作为 File 对象
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult 的方法
     *
     * @param result MultiFileCodeResult 对象，包含生成的 HTML、CSS 和 JavaScript 代码
     * @return 返回保存 HTML、CSS 和 JS 代码的文件夹
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult result) {
        // 构建唯一的目录路径，依据多文件代码的业务类型
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        // 将 HTML 代码写入到该目录下的 "index.html" 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        // 将 CSS 代码写入到该目录下的 "style.css" 文件
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        // 将 JavaScript 代码写入到该目录下的 "script.js" 文件
        writeToFile(baseDirPath, "script.js", result.getJsCode());
        // 返回保存的目录作为 File 对象
        return new File(baseDirPath);
    }

    /**
     * 构建唯一的目录路径方法
     *
     * @param bizType 业务类型，例如 HTML 或 MULTI_FILE，用于文件夹名称
     * @return 返回生成的唯一目录路径
     */
    private static String buildUniqueDir(String bizType) {
        // 使用雪花算法生成一个唯一的 ID 并与业务类型拼接
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        // 拼接根目录和唯一目录名，形成完整的目录路径
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        // 创建该目录
        FileUtil.mkdir(dirPath);
        // 返回生成的目录路径
        return dirPath;
    }

    /**
     * 将内容写入到指定目录下的文件中
     *
     * @param dirPath 目标目录路径
     * @param filename 文件名
     * @param content 文件内容
     */
    private static void writeToFile(String dirPath, String filename, String content) {
        // 拼接出文件的完整路径
        String filePath = dirPath + File.separator + filename;
        // 使用 FileUtil 将内容写入到文件中，采用 UTF-8 编码
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}


