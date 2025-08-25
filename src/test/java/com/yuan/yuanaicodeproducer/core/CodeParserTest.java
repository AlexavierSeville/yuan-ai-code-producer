package com.yuan.yuanaicodeproducer.core;

import com.yuan.yuanaicodeproducer.ai.model.HtmlCodeResult;
import com.yuan.yuanaicodeproducer.ai.model.MultiFileCodeResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeParserTest {

    /**
     * 测试 parseHtmlCode 方法，解析 HTML 代码块
     *
     * @throws Exception 如果测试失败抛出异常
     */
    @Test
    void parseHtmlCode() {
        // 输入包含 HTML 代码块的字符串
        String codeContent = """
                随便写一段描述：
                ```html
                <!DOCTYPE html>
                <html>
                <head>
                    <title>测试页面</title>
                </head>
                <body>
                    <h1>Hello World!</h1>
                </body>
                </html>
                ```
                随便写一段描述
                """;

        // 调用 CodeParser 的 parseHtmlCode 方法解析代码内容
        HtmlCodeResult result = CodeParser.parseHtmlCode(codeContent);

        // 断言结果不为 null，确保解析返回了结果
        assertNotNull(result);

        // 断言解析出来的 HTML 代码不为 null，确保 HTML 代码被正确提取
        assertNotNull(result.getHtmlCode());
    }

    /**
     * 测试 parseMultiFileCode 方法，解析多文件 HTML、CSS 和 JS 代码块
     *
     * @throws Exception 如果测试失败抛出异常
     */
    @Test
    void parseMultiFileCode() {
        // 输入包含 HTML、CSS 和 JS 代码块的字符串
        String codeContent = """
                创建一个完整的网页：
                ```html
                <!DOCTYPE html>
                <html>
                <head>
                    <title>多文件示例</title>
                    <link rel="stylesheet" href="style.css">
                </head>
                <body>
                    <h1>欢迎使用</h1>
                    <script src="script.js"></script>
                </body>
                </html>
                ```
                ```css
                h1 {
                    color: blue;
                    text-align: center;
                }
                ```
                ```js
                console.log('页面加载完成');
                ```
                文件创建完成！
                """;

        // 调用 CodeParser 的 parseMultiFileCode 方法解析多文件代码
        MultiFileCodeResult result = CodeParser.parseMultiFileCode(codeContent);

        // 断言解析结果不为 null
        assertNotNull(result);

        // 断言解析出来的 HTML 代码不为 null
        assertNotNull(result.getHtmlCode());

        // 断言解析出来的 CSS 代码不为 null
        assertNotNull(result.getCssCode());

        // 断言解析出来的 JS 代码不为 null
        assertNotNull(result.getJsCode());
    }
}
