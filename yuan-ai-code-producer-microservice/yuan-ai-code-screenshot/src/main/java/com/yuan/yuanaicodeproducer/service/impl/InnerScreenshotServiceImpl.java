package com.yuan.yuanaicodeproducer.service.impl;

import com.yuan.yuanaicodeproducer.innerservice.InnerScreenshotService;
import com.yuan.yuanaicodeproducer.service.ScreenshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-19 10:17:40
 * @className InnerScreenshotserviceImpl
 * @description
 */
@DubboService
@Slf4j
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        return screenshotService.generateAndUploadScreenshot(webUrl);
    }
}

