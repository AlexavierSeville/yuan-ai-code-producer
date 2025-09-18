package com.yuan.yuanaicodeuser.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.yuan.yuanaicodeproducer.model.dto.user.*;
import com.yuan.yuanaicodeproducer.annotation.AuthCheck;
import com.yuan.yuanaicodeproducer.common.BaseResponse;
import com.yuan.yuanaicodeproducer.common.DeleteRequest;
import com.yuan.yuanaicodeproducer.common.ResultUtils;
import com.yuan.yuanaicodeproducer.constant.UserConstant;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.exception.ThrowUtils;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.vo.LoginUserVO;
import com.yuan.yuanaicodeproducer.model.vo.UserVO;
import com.yuan.yuanaicodeuser.service.UserService;
import com.yuan.yuanaicodeuser.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserController", description = "与用户注册、登录、查询、管理相关的接口")
public class UserController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;

    /**
     * 发送验证码
     * @param sendVerificationCodeRequest
     * @return 发送结果
     */
    @PostMapping("send-verification-code")
    @Operation(
            summary = "发送验证码",
            description = "向指定邮箱发送验证码",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "发送验证码请求体，包含邮箱和验证码类型")
    )
    public BaseResponse<Boolean> sendVerificationCode(@RequestBody SendVerificationCodeRequest sendVerificationCodeRequest) {
        ThrowUtils.throwIf(sendVerificationCodeRequest == null, ErrorCode.PARAMS_ERROR);
        String email = sendVerificationCodeRequest.getEmail();
        String codeType = sendVerificationCodeRequest.getCodeType();
        boolean result = verificationCodeService.sendVerificationCode(email, codeType);
        return ResultUtils.success(result);
    }

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 用户id
     */
    @PostMapping("register")
    @Operation(
            summary = "用户注册",
            description = "根据账号与密码等信息注册用户，返回用户 ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "注册请求体，包含账号、密码、确认密码、验证码等")
    )
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String verificationCode = userRegisterRequest.getVerificationCode();
        long result = userService.userRegister(userAccount, userPassword, checkPassword, verificationCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求
     * @param request 请求
     * @return 登录用户信息
     */
    @PostMapping("login")
    @Operation(
            summary = "用户登录",
            description = "用户使用账号密码登录，成功后在会话中记录登录态并返回登录用户信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "登录请求体，包含账号和密码")
    )
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, @Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     * @param request 请求
     * @return 登录用户信息
     */
    @GetMapping("get/login")
    @Operation(summary = "获取当前登录用户", description = "基于会话获取当前已登录的用户信息")
    public BaseResponse<LoginUserVO> getLoginUser(@Parameter(hidden = true) HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     * @param request 请求
     * @return 退出结果
     */
    @PostMapping("logout")
    @Operation(summary = "用户注销", description = "清除会话中的登录态")
    public BaseResponse<Boolean> userLogout(@Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "创建用户（管理员）",
            description = "管理员创建新用户，默认设置初始密码",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "新增用户请求体")
    )
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptedPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "根据 ID 获取用户（管理员）", description = "管理员根据用户 ID 获取用户原始信息")
    public BaseResponse<User> getUserById(@Parameter(description = "用户 ID", required = true) long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 ID 获取用户包装信息", description = "根据用户 ID 获取脱敏后的用户信息（VO）")
    public BaseResponse<UserVO> getUserVOById(@Parameter(description = "用户 ID", required = true) long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "删除用户（管理员）",
            description = "管理员根据用户 ID 删除用户",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "删除请求体，包含用户 ID")
    )
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "更新用户（管理员）",
            description = "管理员更新用户基础信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "更新请求体，包含用户 ID 和待更新信息")
    )
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "分页获取用户列表（管理员）",
            description = "根据查询条件分页获取用户列表并做脱敏（VO）",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "查询条件，包含页码、页大小等")
    )
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 获取当前用户个人信息
     * @param request 请求
     * @return 用户个人信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户个人信息", description = "获取当前登录用户的个人信息")
    public BaseResponse<UserProfileVO> getCurrentUserProfile(@Parameter(hidden = true) HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserProfileVO userProfile = userService.getUserProfile(loginUser.getId());
        return ResultUtils.success(userProfile);
    }

    /**
     * 更新当前用户个人信息
     * @param userProfileUpdateRequest 更新请求
     * @param request 请求
     * @return 更新结果
     */
    @PostMapping("/profile/update")
    @Operation(
            summary = "更新当前用户个人信息",
            description = "更新当前登录用户的个人信息，包括昵称、头像、简介、密码等",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "个人信息更新请求体")
    )
    public BaseResponse<Boolean> updateCurrentUserProfile(
            @RequestBody UserProfileUpdateRequest userProfileUpdateRequest,
            @Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(userProfileUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.updateUserProfile(loginUser.getId(), userProfileUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param request 请求
     * @return 头像URL
     */
    @PostMapping("/avatar/upload")
    @Operation(
            summary = "上传用户头像",
            description = "上传用户头像到对象存储，返回头像访问URL"
    )
    public BaseResponse<String> uploadUserAvatar(
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) HttpServletRequest request) {
        try {
            ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
            User loginUser = userService.getLoginUser(request);
            String avatarUrl = userService.uploadUserAvatar(file, loginUser.getId());
            return ResultUtils.success(avatarUrl);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw e;
        }
    }

}
