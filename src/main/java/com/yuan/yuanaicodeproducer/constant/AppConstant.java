package com.yuan.yuanaicodeproducer.constant;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-26 15:06:34
 * @className AppConstant
 * @description 应用常量类
 * 
 * 功能说明：
 * 1. 定义应用相关的全局常量
 * 2. 包含应用优先级、目录路径、域名等配置
 * 3. 为缓存机制提供精选应用的优先级标识
 */
public interface AppConstant {

    /**
     * 精选应用的优先级
     * 
     * 用途：
     * - 标识精选应用，用于缓存查询条件
     * - 在分页获取精选应用列表时作为查询条件
     * - 数值越大优先级越高，精选应用优先级最高
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     * 
     * 用途：
     * - 普通应用的默认优先级
     * - 用于应用排序和显示顺序控制
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用代码生成根目录
     * 
     * 路径说明：
     * - 基于项目根目录的tmp/code_output文件夹
     * - 存储AI生成的代码文件
     * - 用于预览和下载功能
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署根目录
     * 
     * 路径说明：
     * - 基于项目根目录的tmp/code_deploy文件夹
     * - 存储部署后的应用文件
     * - 用于生产环境访问
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     * 
     * 用途：
     * - 生成应用部署后的访问URL
     * - 用于前端预览和外部访问
     */
    String CODE_DEPLOY_HOST = "http://localhost:8701";

}

