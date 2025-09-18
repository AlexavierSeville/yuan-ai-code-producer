package com.yuan.yuanaicodeuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LXY
 * @version 1.0
 * @date 2025-09-18 15:28:25
 * @className YuanAiCodeUserApplication
 * @description 启动类
 */
@SpringBootApplication
@MapperScan("com.yuan.yuanaicodeuser.mapper")
@ComponentScan("com.yuan")
//@EnableDubbo
public class YuanAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuanAiCodeUserApplication.class, args);
    }
}

