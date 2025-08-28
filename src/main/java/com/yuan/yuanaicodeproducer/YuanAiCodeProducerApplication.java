package com.yuan.yuanaicodeproducer;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.yuan.yuanaicodeproducer.mapper")
public class YuanAiCodeProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuanAiCodeProducerApplication.class, args);
	}

}
