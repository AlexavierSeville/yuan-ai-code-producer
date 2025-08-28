package com.yuan.yuanaicodeproducer.controller;

import com.yuan.yuanaicodeproducer.common.BaseResponse;
import com.yuan.yuanaicodeproducer.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 10:41:00
 * @className HealthController
 * @description 健康检查
 */
@RestController
@RequestMapping("/health")
@Tag(name = "HealthController", description = "系统自检与可用性探测接口")
public class HealthController {
    @GetMapping("")
    @Operation(summary = "健康检查", description = "返回 OK 表示服务可用")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
