package com.yuan.yuanaicodeproducer.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuan.yuanaicodeproducer.model.dto.app.AppQueryRequest;
import com.yuan.yuanaicodeproducer.model.entity.App;
import com.yuan.yuanaicodeproducer.model.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用封装类
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * *****获取应用封装类列表*****
     * 分页查询应用时，也需要额外获取创建应用的用户信息，
     * 这会涉及到关联查询多个用户信息，我们需要优化查询性能。优化查询逻辑如下:
     * 1. 先收集所有 userld 到集合中
     * 2. 根据 userld 集合批量查询所有用户信息
     * 3. 构建 Map 映射关系 userld =>UserVO
     * 4. 一次性组装所有 AppVO，根据 userld 从 Map 中取到需要的用户信息
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);



}
