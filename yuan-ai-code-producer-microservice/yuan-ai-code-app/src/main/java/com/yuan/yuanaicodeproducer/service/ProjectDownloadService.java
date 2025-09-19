package com.yuan.yuanaicodeproducer.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-02 08:51:08
 * @className ProjectDownloadService
 * @description 代码文件下载
 */
public interface ProjectDownloadService {

    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
