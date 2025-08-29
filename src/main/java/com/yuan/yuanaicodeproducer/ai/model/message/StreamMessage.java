package com.yuan.yuanaicodeproducer.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-08-29 15:08:08
 * @className StreamMessage
 * @description 流式消息响应基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {
    private String type;
}

