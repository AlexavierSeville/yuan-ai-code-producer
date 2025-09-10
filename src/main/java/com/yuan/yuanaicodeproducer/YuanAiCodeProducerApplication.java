package com.yuan.yuanaicodeproducer;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.yuan.yuanaicodeproducer.mapper")
@EnableCaching  // 开启Spring Cache缓存注解支持，启用@Cacheable、@CacheEvict等注解
public class YuanAiCodeProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuanAiCodeProducerApplication.class, args);
	}

}
