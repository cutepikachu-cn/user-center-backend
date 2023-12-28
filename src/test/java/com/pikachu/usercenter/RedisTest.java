package com.pikachu.usercenter;

import com.pikachu.usercenter.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // ListOperations listOperations = redisTemplate.opsForList();
        valueOperations.set("pikachu_string", "string");
        valueOperations.set("pikachu_integer", 123456);
        valueOperations.set("pikachu_double", 3.1345);
        valueOperations.set("pikachu_user", new User());

        System.out.println(valueOperations.get("pikachu_string"));
        System.out.println(valueOperations.get("pikachu_integer"));
        System.out.println(valueOperations.get("pikachu_double"));
        System.out.println(valueOperations.get("pikachu_user"));
    }
}
