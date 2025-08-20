package com.yuan.yuanaicodeproducer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuan.yuanaicodeproducer.mapper")
public class YuanAiCodeProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuanAiCodeProducerApplication.class, args);
	}

}
