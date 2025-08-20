package com.yuan.yuanaicodeproducer.model.dto;

import com.yuan.yuanaicodeproducer.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-08-19 17:26:20
 * @className UserQueryRequest
 * @description 用户查询请求
 */
// 自动生成 equals() 和 hashCode() 方法，并确保在比较子类对象时同时考虑父类的字段。
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 3791271717373120793L;
}
