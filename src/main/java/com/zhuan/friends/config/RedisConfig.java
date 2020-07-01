package com.zhuan.friends.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis配置类 由于集成的redis中键的类型是Object类型不易使用我们修改其配置为 String 类型
 */
@Configuration
public class RedisConfig {

    /**
     * 参数中声明 redis连接的工厂之后Spring会将其自动注入使用
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置特殊格式hashes的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hashes的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //设置完成之后需要将 设置的配置初始化
        template.afterPropertiesSet();
        return template;
    }
}
