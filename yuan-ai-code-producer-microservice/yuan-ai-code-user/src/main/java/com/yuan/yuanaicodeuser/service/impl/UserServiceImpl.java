package com.yuan.yuanaicodeuser.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.exception.ThrowUtils;
import com.yuan.yuanaicodeproducer.manager.CosManager;
import com.yuan.yuanaicodeuser.mapper.UserMapper;
import com.yuan.yuanaicodeproducer.model.dto.user.UserProfileUpdateRequest;
import com.yuan.yuanaicodeproducer.model.dto.user.UserProfileVO;
import com.yuan.yuanaicodeproducer.model.dto.user.UserQueryRequest;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.enums.UserRoleEnum;
import com.yuan.yuanaicodeproducer.model.vo.LoginUserVO;
import com.yuan.yuanaicodeproducer.model.vo.UserVO;
import com.yuan.yuanaicodeuser.service.UserService;
import com.yuan.yuanaicodeuser.service.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.yuan.yuanaicodeproducer.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    private final CosManager cosManager;
    private final VerificationCodeService verificationCodeService;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 查询用户是否已经存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
//        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }
        // 3. 密码加密
        String encryptedPassword = getEncryptedPassword(userPassword);
        // 4. 创建用户，插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName(userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败,数据库错误");
        }
        return user.getId();

    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String verificationCode) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword, verificationCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 2. 验证邮箱格式
        if (!isValidEmail(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入有效的邮箱地址");
        }
        
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 3. 验证验证码
        verificationCodeService.verifyCode(userAccount, verificationCode, "REGISTER");

        // 4. 查询用户是否已经存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }
        
        // 5. 密码加密
        String encryptedPassword = getEncryptedPassword(userPassword);
        
        // 6. 创建用户，插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName(userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败,数据库错误");
        }
        return user.getId();
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 2. 加密
        String encryptedPassword = getEncryptedPassword(userPassword);
        // 3. 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptedPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 如果用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {

        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .toList();
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    // 密码加密
    @Override
    public String getEncryptedPassword(String userPassword){
        // 盐值，混淆密码
        final String SALT = "yuan";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, userProfileVO);
        return userProfileVO;
    }

    @Override
    public boolean updateUserProfile(Long userId, UserProfileUpdateRequest userProfileUpdateRequest) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (userProfileUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新请求为空");
        }
        
        // 检查用户是否存在
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 更新用户信息
        if (StrUtil.isNotBlank(userProfileUpdateRequest.getUserName())) {
            user.setUserName(userProfileUpdateRequest.getUserName());
        }
        if (StrUtil.isNotBlank(userProfileUpdateRequest.getUserAvatar())) {
            user.setUserAvatar(userProfileUpdateRequest.getUserAvatar());
        }
        if (StrUtil.isNotBlank(userProfileUpdateRequest.getUserProfile())) {
            user.setUserProfile(userProfileUpdateRequest.getUserProfile());
        }
        
        // 如果提供了新密码，则更新密码
        if (StrUtil.isNotBlank(userProfileUpdateRequest.getUserPassword())) {
            String newPassword = userProfileUpdateRequest.getUserPassword();
            String checkPassword = userProfileUpdateRequest.getCheckPassword();
            
            if (StrUtil.isBlank(checkPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "确认密码不能为空");
            }
            if (!newPassword.equals(checkPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
            }
            if (newPassword.length() < 8) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能少于8位");
            }
            
            user.setUserPassword(getEncryptedPassword(newPassword));
        }
        
        return this.updateById(user);
    }

    @Override
    public String uploadUserAvatar(MultipartFile file, Long userId) {
        log.info("=== 开始头像上传调试 ===");
        log.info("用户ID: {}", userId);
        log.info("文件名: {}", file.getOriginalFilename());
        log.info("文件大小: {} bytes", file.getSize());
        log.info("文件类型: {}", file.getContentType());
        
        ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID无效");
        
        log.info("开始上传用户头像，用户ID: {}", userId);
        
        // 1. 生成本地临时文件
        String localFilePath = saveMultipartFileToLocal(file);
        log.info("本地文件路径: {}", localFilePath);
        ThrowUtils.throwIf(StrUtil.isBlank(localFilePath), ErrorCode.OPERATION_ERROR, "本地文件保存失败");
        
        try {
            // 2. 上传到对象存储
            String avatarUrl = uploadAvatarToCos(localFilePath, userId);
            log.info("COS返回URL: {}", avatarUrl);
            ThrowUtils.throwIf(StrUtil.isBlank(avatarUrl), ErrorCode.OPERATION_ERROR, "头像上传对象存储失败");
            log.info("用户头像上传成功，用户ID: {} -> {}", userId, avatarUrl);
            return avatarUrl;
        } finally {
            // 3. 清理本地文件
            cleanupLocalFile(localFilePath);
        }
    }

    /**
     * 保存MultipartFile到本地临时文件
     *
     * @param file 上传的文件
     * @return 本地文件路径，失败返回null
     */
    private String saveMultipartFileToLocal(MultipartFile file) {
        try {
            log.info("=== 保存临时文件调试 ===");
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            log.info("原始文件名: {}", originalFilename);
            if (originalFilename == null) {
                log.error("文件名为空");
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
            }
            
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            log.info("文件后缀: {}", suffix);
            if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") 
                && !suffix.equalsIgnoreCase(".png") && !suffix.equalsIgnoreCase(".gif")) {
                log.error("不支持的文件格式: {}", suffix);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "只支持jpg、jpeg、png、gif格式的图片");
            }
            
            // 检查文件大小（限制为5MB）
            long fileSize = file.getSize();
            log.info("文件大小: {} bytes ({} MB)", fileSize, fileSize / 1024.0 / 1024.0);
            if (fileSize > 5 * 1024 * 1024) {
                log.error("文件过大: {} bytes", fileSize);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");
            }
            
            // 创建临时文件
            File tempFile = File.createTempFile("avatar_", suffix);
            log.info("创建临时文件: {}", tempFile.getAbsolutePath());
            file.transferTo(tempFile);
            log.info("文件传输完成，临时文件大小: {} bytes", tempFile.length());
            
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("保存临时文件失败", e);
            return null;
        }
    }

    /**
     * 上传头像到对象存储
     *
     * @param localFilePath 本地文件路径
     * @param userId 用户ID
     * @return 对象存储访问URL，失败返回null
     */
    private String uploadAvatarToCos(String localFilePath, Long userId) {
        log.info("=== 上传到COS调试 ===");
        log.info("本地文件路径: {}", localFilePath);
        
        if (StrUtil.isBlank(localFilePath)) {
            log.error("本地文件路径为空");
            return null;
        }
        File avatarFile = new File(localFilePath);
        log.info("文件是否存在: {}", avatarFile.exists());
        log.info("文件大小: {} bytes", avatarFile.length());
        
        if (!avatarFile.exists()) {
            log.error("头像文件不存在: {}", localFilePath);
            return null;
        }
        
        // 生成 COS 对象键
        String fileName = generateAvatarFileName(userId);
        String cosKey = generateAvatarKey(fileName);
        log.info("生成的文件名: {}", fileName);
        log.info("生成的COS键: {}", cosKey);
        
        try {
            String result = cosManager.uploadFile(cosKey, avatarFile);
            log.info("COS上传结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("COS上传异常", e);
            return null;
        }
    }

    /**
     * 生成头像文件名
     * 格式：userId_uuid.jpg
     */
    private String generateAvatarFileName(Long userId) {
        String suffix = ".jpg"; // 统一使用jpg格式
        return userId + "_" + UUID.randomUUID().toString().substring(0, 8) + suffix;
    }

    /**
     * 生成头像的对象存储键
     * 格式：/user_avatar/2025/01/15/filename.jpg
     */
    private String generateAvatarKey(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("/user_avatar/%s/%s", datePath, fileName);
    }

    /**
     * 清理本地文件
     *
     * @param localFilePath 本地文件路径
     */
    private void cleanupLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            boolean deleted = localFile.delete();
            if (deleted) {
                log.info("本地头像文件已清理: {}", localFilePath);
            } else {
                log.warn("本地头像文件清理失败: {}", localFilePath);
            }
        }
    }

}
