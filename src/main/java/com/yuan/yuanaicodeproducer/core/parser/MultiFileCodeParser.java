package com.yuan.yuanaicodeproducer.core.parser;

import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-25 16:37:44
 * @className MultiFileCodeParser
 * @description 多文件代码解析器
 */
@Slf4j
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult> {

    // 代码解析专用线程池
    private static final ExecutorService parseExecutor = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "CodeParse-");
        t.setDaemon(true);
        return t;
    });

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    @Override
    public MultiFileCodeResult parseCode(String codeContent) {
        log.info("开始并发解析多文件代码");
        
        MultiFileCodeResult result = new MultiFileCodeResult();
        
        // 创建并发解析任务
        CompletableFuture<String> htmlTask = CompletableFuture.supplyAsync(() -> {
            String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
            log.debug("HTML代码解析完成");
            return htmlCode;
        }, parseExecutor);
        
        CompletableFuture<String> cssTask = CompletableFuture.supplyAsync(() -> {
            String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
            log.debug("CSS代码解析完成");
            return cssCode;
        }, parseExecutor);
        
        CompletableFuture<String> jsTask = CompletableFuture.supplyAsync(() -> {
            String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);
            log.debug("JS代码解析完成");
            return jsCode;
        }, parseExecutor);
        
        // 等待所有解析任务完成
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(htmlTask, cssTask, jsTask);
        allTasks.join();
        
        // 设置解析结果
        try {
            String htmlCode = htmlTask.get();
            if (htmlCode != null && !htmlCode.trim().isEmpty()) {
                result.setHtmlCode(htmlCode.trim());
            }
            
            String cssCode = cssTask.get();
            if (cssCode != null && !cssCode.trim().isEmpty()) {
                result.setCssCode(cssCode.trim());
            }
            
            String jsCode = jsTask.get();
            if (jsCode != null && !jsCode.trim().isEmpty()) {
                result.setJsCode(jsCode.trim());
            }
        } catch (Exception e) {
            log.error("代码解析异常", e);
        }
        
        log.info("多文件代码并发解析完成");
        return result;
    }

    /**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码
     */
    private String extractCodeByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

