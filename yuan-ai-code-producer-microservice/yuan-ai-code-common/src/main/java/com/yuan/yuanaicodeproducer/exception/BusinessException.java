package com.yuan.yuanaicodeproducer.exception;

import lombok.Getter;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 10:55:42
 * @className BusinessException
 * @description 自定义异常类
 */
@Getter
public class BusinessException extends RuntimeException{
    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
