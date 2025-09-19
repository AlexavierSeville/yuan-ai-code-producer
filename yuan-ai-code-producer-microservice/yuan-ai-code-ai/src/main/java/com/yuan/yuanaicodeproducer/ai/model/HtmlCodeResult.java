package com.yuan.yuanaicodeproducer.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 15:27:44
 * @className HtmlCodeResult
 * @description 把AI的输出转换成结构化的对象
 */
// 这里是langchain的Description注解
@Description("生成 HTML 代码文件的结果")
@Data
public class HtmlCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("生成代码的描述")
    private String description;
}

