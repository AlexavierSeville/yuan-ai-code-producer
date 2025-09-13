package com.yuan.yuanaicodeproducer.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-26 09:02:19
 * @className MultiFileCodeFileSaverTemplate
 * @description 把之前保存多文件代码的逻辑放到这
 */
@Slf4j
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {

    // 文件保存专用线程池
    private static final ExecutorService fileSaveExecutor = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "FileSave-");
        t.setDaemon(true);
        return t;
    });

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        log.info("开始并发保存多文件代码");
        
        // 创建并发保存任务
        CompletableFuture<Void> htmlTask = CompletableFuture.runAsync(() -> {
            if (StrUtil.isNotBlank(result.getHtmlCode())) {
                writeToFile(baseDirPath, "index.html", result.getHtmlCode());
                log.debug("HTML文件保存完成");
            }
        }, fileSaveExecutor);
        
        CompletableFuture<Void> cssTask = CompletableFuture.runAsync(() -> {
            if (StrUtil.isNotBlank(result.getCssCode())) {
                writeToFile(baseDirPath, "style.css", result.getCssCode());
                log.debug("CSS文件保存完成");
            }
        }, fileSaveExecutor);
        
        CompletableFuture<Void> jsTask = CompletableFuture.runAsync(() -> {
            if (StrUtil.isNotBlank(result.getJsCode())) {
                writeToFile(baseDirPath, "script.js", result.getJsCode());
                log.debug("JS文件保存完成");
            }
        }, fileSaveExecutor);
        
        // 等待所有文件保存完成
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(htmlTask, cssTask, jsTask);
        allTasks.join();
        
        log.info("多文件代码并发保存完成");
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        // 至少要有 HTML 代码，CSS 和 JS 可以为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}

