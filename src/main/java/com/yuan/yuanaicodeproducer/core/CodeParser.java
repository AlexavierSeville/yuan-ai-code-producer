package com.yuan.yuanaicodeproducer.core;

import com.yuan.yuanaicodeproducer.ai.model.HtmlCodeResult;
import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-25 10:45:20
 * @className CodeParser；已弃用，新方法定义于parser包内
 * @description 代码解析器,提供静态方法解析不同类型的代码内容
 */
@Deprecated
public class CodeParser {

    // 正则表达式模式，用于匹配 HTML 代码块
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 正则表达式模式，用于匹配 CSS 代码块
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 正则表达式模式，用于匹配 JS 代码块，支持 js 或 javascript
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    /**
     * 解析 HTML 单文件代码
     *
     * @param codeContent 输入的代码内容，可能包含 HTML 代码
     * @return HtmlCodeResult 返回解析出的 HTML 代码封装对象
     */
    public static HtmlCodeResult parseHtmlCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            // 如果提取到 HTML 代码，将其去除空白并设置到结果中
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到 HTML 代码块，返回整个内容作为 HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }

    /**
     * 解析多文件代码（HTML + CSS + JS）
     *
     * @param codeContent 输入的代码内容，可能包含 HTML、CSS 和 JavaScript 代码
     * @return MultiFileCodeResult 返回解析出的 HTML、CSS 和 JS 代码封装对象
     */
    public static MultiFileCodeResult parseMultiFileCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取 HTML、CSS 和 JS 代码
        String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);
        // 设置提取到的 HTML 代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        }
        // 设置提取到的 CSS 代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        }
        // 设置提取到的 JS 代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        }
        return result;
    }

    /**
     * 提取 HTML 代码内容
     *
     * @param content 原始内容，可能包含 HTML 代码
     * @return 提取到的 HTML 代码，若未找到则返回 null
     */
    private static String extractHtmlCode(String content) {
        // 使用 HTML_CODE_PATTERN 正则表达式匹配 HTML 代码
        Matcher matcher = HTML_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            // 返回匹配到的 HTML 代码块
            return matcher.group(1);
        }
        return null; // 如果没有找到匹配的代码，返回 null
    }

    /**
     * 根据正则模式提取指定的代码块
     *
     * @param content 原始内容，可能包含多种代码块（HTML、CSS、JS）
     * @param pattern 正则模式，用于匹配代码块
     * @return 提取到的代码块，若未找到则返回 null
     */
    private static String extractCodeByPattern(String content, Pattern pattern) {
        // 使用指定的正则模式匹配内容
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            // 返回匹配到的代码块
            return matcher.group(1);
        }
        return null; // 如果没有找到匹配的代码块，返回 null
    }
}


