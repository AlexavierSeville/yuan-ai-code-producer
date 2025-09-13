package com.yuan.yuanaicodeproducer.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuan.yuanaicodeproducer.model.dto.user.UserQueryRequest;
import com.yuan.yuanaicodeproducer.model.dto.user.UserProfileUpdateRequest;
import com.yuan.yuanaicodeproducer.model.dto.user.UserProfileVO;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.vo.LoginUserVO;
import com.yuan.yuanaicodeproducer.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户注册（带验证码）
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @param verificationCode 验证码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String verificationCode);

    /**
     * 获取脱敏后的用户登录信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request 请求
     * @return 登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     * @param request 请求
     * @return 登出结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的登录用户
     * @param user 用户
     * @return 脱敏用户
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户列表
     * @param userList 用户列表
     * @return 脱敏用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 根据查询条件构造数据查询参数
     * @param userQueryRequest 查询条件
     * @return 数据查询参数
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取加密密码
     * @param userPassword
     * @return
     */
    String getEncryptedPassword(String userPassword);

    /**
     * 获取用户个人信息
     * @param userId 用户ID
     * @return 用户个人信息
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param userProfileUpdateRequest 更新请求
     * @return 是否成功
     */
    boolean updateUserProfile(Long userId, UserProfileUpdateRequest userProfileUpdateRequest);

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像URL
     */
    String uploadUserAvatar(MultipartFile file, Long userId);

}
