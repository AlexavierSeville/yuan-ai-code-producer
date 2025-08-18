package com.yuan.yuanaicodeproducer.exception;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-18 10:58:36
 * @className ThrowUtils
 * @description 用于抛出异常的工具类
 */
public class ThrowUtils {
    /**
     * 条件成立则抛出异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛出异常
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛出异常
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
