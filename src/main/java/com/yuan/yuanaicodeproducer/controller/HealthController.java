package com.yuan.yuanaicodeproducer.controller;

import com.yuan.yuanaicodeproducer.common.BaseResponse;
import com.yuan.yuanaicodeproducer.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 10:41:00
 * @className HealthController
 * @description 健康检查
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping("")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
