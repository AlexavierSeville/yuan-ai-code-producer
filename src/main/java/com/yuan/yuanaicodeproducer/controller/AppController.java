package com.yuan.yuanaicodeproducer.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.yuan.yuanaicodeproducer.annotation.AuthCheck;
import com.yuan.yuanaicodeproducer.common.BaseResponse;
import com.yuan.yuanaicodeproducer.common.DeleteRequest;
import com.yuan.yuanaicodeproducer.common.ResultUtils;
import com.yuan.yuanaicodeproducer.constant.AppConstant;
import com.yuan.yuanaicodeproducer.constant.UserConstant;
import com.yuan.yuanaicodeproducer.exception.BusinessException;
import com.yuan.yuanaicodeproducer.exception.ErrorCode;
import com.yuan.yuanaicodeproducer.exception.ThrowUtils;
import com.yuan.yuanaicodeproducer.model.dto.app.*;
import com.yuan.yuanaicodeproducer.model.entity.User;
import com.yuan.yuanaicodeproducer.model.enums.CodeGenTypeEnum;
import com.yuan.yuanaicodeproducer.model.vo.AppVO;
import com.yuan.yuanaicodeproducer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import com.yuan.yuanaicodeproducer.model.entity.App;
import com.yuan.yuanaicodeproducer.service.AppService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
// @Tag(name = "应用接口", description = "与应用创建、部署、查询和生成代码相关的接口")
public class AppController {

    private final AppService appService;
    private final UserService userService;

    /**
     * 应用聊天生成代码（流式 SSE）
     * produces = MediaType.TEXT_EVENT_STREAM_VALUE是响应类型，表示返回的是 SSE 数据
     * @param appId   应用 ID
     * @param message 用户消息
     * @param request 请求对象
     * @return 生成结果流
     * # 1. 用户登录
     * curl -X POST "http://localhost:8700/yuan/user/login" \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *     "userAccount": "yuan",
     *     "userPassword": "12345678"
     *   }' \
     *   -c cookies.txt
     *
     * # 2. 调用生成代码接口（流式）
     * curl -G "http://localhost:8700/yuan/app/chat/gen/code" \
     *   --data-urlencode "appId=318133222387093504" \
     *   --data-urlencode "message=做一个个人博客，总代码行数不超过30行" \
     *   -H "Accept: text/event-stream" \
     *   -H "Cache-Control: no-cache" \
     *   -b cookies.txt \
     *   --no-buffer
     */

    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "应用聊天生成代码（SSE 流）",
            description = "用户登录后，基于指定的应用 ID 和用户消息实时生成代码，返回 Server-Sent Events 流以便前端逐步渲染。"
    )
    public Flux<ServerSentEvent<String>> chatToGenCode(
                                      @Parameter(description = "应用 ID", required = true)
                                      @RequestParam Long appId,
                                      @Parameter(description = "用户输入的需求/提示词", required = true)
                                      @RequestParam String message,
                                      @Parameter(hidden = true) HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务生成代码（流式）
        Flux<String> contentFlux = appService.chatToGenCode(appId, message, loginUser);
        // 因为流式的内容返回前端时可能会出现空格丢失的问题，导致出现例如 <divclass... 之类的东西，所以在这格式化输出
        return contentFlux
                .map(chunk -> {
                    // 将内容包装成JSON对象，放入"d"字段是为了尽可能短，以节省开销
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    // ServerSentEvent 是 Spring WebFlux 提供的 SSE 封装类，用来构建服务端事件对象。
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 当大模型输出完成后发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }


    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       请求
     * @return 应用 id
     */
    @PostMapping("/add")
    @Operation(
            summary = "创建应用",
            description = "根据初始化 Prompt 创建一个新的应用，默认使用多文件生成模式",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "创建应用请求体，需提供初始化 Prompt 等必填信息")
    )
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, @Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 构造入库对象
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // 应用名称暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 暂时设置为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        // 插入数据库
        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(app.getId());
    }

    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @param request          请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    @Operation(
            summary = "应用部署",
            description = "部署指定应用并返回可访问的部署地址",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "部署请求体，包含应用 ID 等信息")
    )
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, @Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deployUrl);
    }


    /**
     * 删除应用（用户只能删除自己的应用）
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    @Operation(
            summary = "删除应用（仅本人或管理员）",
            description = "根据应用 ID 删除应用，只有应用创建者本人或管理员可操作",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "删除请求体，需提供要删除的应用 ID")
    )
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, @Parameter(hidden = true) HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldApp.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = appService.removeById(id);
        return ResultUtils.success(result);
    }


    /**
     * 更新应用（用户只能更新自己的应用名称）
     * 用户更新应用时，需要进行权限校验，确保只能修改自己的应用，
     * @param appUpdateRequest 更新请求
     * @param request          请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(
            summary = "更新应用（仅本人）",
            description = "仅允许应用创建者更新应用名称等基本信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "更新请求体，包含应用 ID 和待更新信息")
    )
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, @Parameter(hidden = true) HttpServletRequest request) {
        if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = appUpdateRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可更新
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        App app = new App();
        app.setId(id);
        app.setAppName(appUpdateRequest.getAppName());
        // 设置编辑时间
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 查询所有应用。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(summary = "查询所有应用", description = "获取系统中所有应用的原始数据列表（非分页）")
    public List<App> list() {
        return appService.list();
    }

    /**
     * 分页获取当前用户创建的应用列表
     *
     * @param appQueryRequest 查询请求
     * @param request         请求
     * @return 应用列表
     */
    @PostMapping("/my/list/page/vo")
    @Operation(
            summary = "分页获取当前用户的应用列表",
            description = "仅返回当前登录用户创建的应用列表（封装 VO）",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "查询条件，包含页码、页大小等")
    )
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest, @Parameter(hidden = true) HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 限制每页最多 20 个
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        long pageNum = appQueryRequest.getPageNum();
        // 只查询当前用户的应用
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 精选应用列表
     */
    @PostMapping("/good/list/page/vo")
    @Operation(
            summary = "分页获取精选应用列表",
            description = "仅返回设为精选的应用列表（封装 VO）",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "查询条件，包含页码、页大小等")
    )
    public BaseResponse<Page<AppVO>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 限制每页最多 20 个
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        long pageNum = appQueryRequest.getPageNum();
        // 只查询精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        // 分页查询
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 管理员删除应用
     *
     * @param deleteRequest 删除请求
     * @return 删除结果
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "管理员删除应用",
            description = "管理员根据应用 ID 删除任意应用",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "删除请求体，需提供要删除的应用 ID")
    )
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = appService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 更新请求
     * @return 更新结果
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "管理员更新应用",
            description = "管理员可更新应用的相关信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "更新请求体，包含应用 ID 和待更新信息")
    )
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = appAdminUpdateRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        // 设置编辑时间
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员分页获取应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用列表
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(
            summary = "管理员分页获取应用列表",
            description = "根据查询条件分页获取应用列表（封装 VO），仅管理员可用",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "查询条件，包含页码、页大小等")
    )
    public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 根据 id 获取应用详情
     *
     * @param id      应用 id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 ID 获取应用详情", description = "根据应用 ID 获取应用的详细信息（封装 VO）")
    public BaseResponse<AppVO> getAppVOById(@Parameter(description = "应用 ID", required = true) long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类（包含用户信息）
        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * 管理员根据 id 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员根据 ID 获取应用详情", description = "管理员可获取任意应用的详细信息（封装 VO）")
    public BaseResponse<AppVO> getAppVOByIdByAdmin(@Parameter(description = "应用 ID", required = true) long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));
    }



}
