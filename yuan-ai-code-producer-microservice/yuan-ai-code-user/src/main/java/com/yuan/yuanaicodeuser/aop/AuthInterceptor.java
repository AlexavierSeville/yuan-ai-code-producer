package com.yuan.yuanaicodeuser.aop;

import com.yuan.yuanaicodeproducer.annotation.AuthCheck;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.enums.UserRoleEnum;
import com.yuan.yuanaicodeuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-08-19 15:04:56
 * @className AuthInterceptor
 * @description 权限校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthInterceptor {

    private final UserService userService;

    // 定义切面
    // 定义切面作用范围
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUserVO = userService.getLoginUser(request);
        // 判断用户是否已经登录
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，直接放行
        if (mustRoleEnum == null){
            return joinPoint.proceed();
        }
        // 以下的代码必须有某个权限才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUserVO.getUserRole());
        // 没有权限，直接拒绝
        if (userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }

}
