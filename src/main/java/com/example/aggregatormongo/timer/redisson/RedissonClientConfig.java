package com.example.aggregatormongo.timer.redisson;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class RedissonClientConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedissonClient createRedisClient() {

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+ redisProperties.getHost()+":"+redisProperties.getPort());
        return Redisson.create(config);
    }
}
