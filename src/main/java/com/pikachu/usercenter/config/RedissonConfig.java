package com.pikachu.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonConfig {
    private Integer port;
    private String host;
    private Integer database;

    @Bean
    public RedissonClient RedissonClient() {
        // 1. Create config object
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%d", host, port);
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(database);

        // 2. Create Redisson instance
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
