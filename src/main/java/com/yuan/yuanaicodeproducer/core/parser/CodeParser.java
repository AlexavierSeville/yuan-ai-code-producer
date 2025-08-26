package com.yuan.yuanaicodeproducer.core.parser;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-25 16:24:14
 * @className CodeParser
 * @description 代码解析器（泛型）
 * 使用策略模式，不同类型的解析策略独立维护
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     *
     * @param codeContent 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String codeContent);
}

