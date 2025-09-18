package com.yuan.yuanaicodeproducer.innerservice;

import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.yuan.yuanaicodeproducer.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-18 15:03:48
 * @className InnerUserService
 * @description 内部调用的用户服务
 */
public interface InnerUserService {

    List<User> listByIds(Collection<? extends Serializable> ids);

    User getById(Serializable id);

    UserVO getUserVO(User user);

    // 静态方法，避免跨服务调用,因为 HttpServletRequest 对象不好在网络中传递
    // （此对象与特定的 HTTP 连接绑定，包含网络套接字、输入输出流等不可序列化的对象） 
    // 每个服务只处理自己职责范围内的事情
    static User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}

