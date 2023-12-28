package com.pikachu.usercenter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
@Slf4j
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        RList<String> rList = redissonClient.getList("test_list");
        rList.add("pikachu1");
        rList.add("pikachu2");
        rList.add("pikachu3");
        rList.add("pikachu4");
        System.out.println(rList.get(2));
        rList.remove(3);
        System.out.println(rList);
    }

    @Test
    void watchDog() throws InterruptedException {
        // 获取锁
        RLock lock = redissonClient.getLock("user-center:user:search:do-ache");
        // 尝试获取锁
        // 为锁添加过期时间
        if (!lock.tryLock(0, 0, TimeUnit.SECONDS)) {
            return;
        }
        try {
            log.info("begin doCacheRecommend");
            Thread.sleep(100000);
        } catch (Exception e) {
            log.error("redis set key error", e);
        } finally {
            // 释放锁
            // 判断锁是否被当前线程所持有
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }
}
