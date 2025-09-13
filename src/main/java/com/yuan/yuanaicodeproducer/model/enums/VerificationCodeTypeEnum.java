package com.yuan.yuanaicodeproducer.model.enums;

import lombok.Getter;

/**
 * 验证码类型枚举
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Getter
public enum VerificationCodeTypeEnum {

    REGISTER("REGISTER", "注册"),
    RESET_PASSWORD("RESET_PASSWORD", "重置密码");

    private final String value;

    private final String text;

    VerificationCodeTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static VerificationCodeTypeEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (VerificationCodeTypeEnum anEnum : VerificationCodeTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
