package com.yuan.yuanaicodeproducer.exception;

import com.yuan.yuanaicodeproducer.common.BaseResponse;
import com.yuan.yuanaicodeproducer.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 11:08:12
 * @className GlobalExceptionHandler
 * @description 全局异常处理器类
 */
// 注意!由于本项目使用的 Spring Boot 版本 >= 3.4、
// 并且是 OpenAPI 3版本的 Knife4j，这会导致 @RestcontrollerAdvice 注解不兼容，
// 所以必须给这个类加上 @Hidden 注解，不被 Swagger 加载。
@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}

