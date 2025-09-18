package com.yuan.yuanaicodeproducer.common;

import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 11:04:16
 * @className BaseResponse
 * @description 通用响应类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

