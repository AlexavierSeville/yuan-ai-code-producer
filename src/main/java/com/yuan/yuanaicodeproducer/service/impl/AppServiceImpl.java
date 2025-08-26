package com.yuan.yuanaicodeproducer.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuan.yuanaicodeproducer.model.entity.App;
import com.yuan.yuanaicodeproducer.mapper.AppMapper;
import com.yuan.yuanaicodeproducer.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://alexavieryuan.us.kg/">元仔学习</a>
 * @since 2025
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
