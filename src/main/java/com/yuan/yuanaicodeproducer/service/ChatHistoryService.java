package com.yuan.yuanaicodeproducer.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuan.yuanaicodeproducer.model.dto.chathistory.ChatHistoryQueryRequest;
import com.yuan.yuanaicodeproducer.model.entity.ChatHistory;
import com.yuan.yuanaicodeproducer.model.entity.User;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId       应用 ID
     * @param userId      用户 ID
     * @param message     消息内容
     * @param messageType 消息类型
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, Long userId, String message, String messageType);

    /**
     * 根据应用 ID 删除对话历史
     *
     * @param appId 应用 ID
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询某AppId的对话聊天记录
     *
     * @param appId       应用 ID
     * @param pageSize    分页大小
     * @param lastCreateTime 最后创建时间
     * @param loginUser   登录用户
     * @return 分页结果
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 获取查询包装类
     * @param chatHistoryQueryRequest 查询条件
     * @return 查询包装类
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

}
