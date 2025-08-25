package com.yuan.yuanaicodeproducer.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-22 15:47:20
 * @className MutiFileCodeResult
 * @description 封装AI返回的内容
 */
@Description("生成多个代码文件的结果")
@Data
public class MultiFileCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("CSS代码")
    private String cssCode;

    @Description("JS代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;
}


