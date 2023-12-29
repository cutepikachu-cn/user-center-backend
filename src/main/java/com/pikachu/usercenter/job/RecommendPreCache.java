package com.pikachu.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Component
@Slf4j
public class RecommendPreCache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 主页推荐定时任务缓存
     * 每天缓存 5 页
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void doCacheRecommend() throws InterruptedException {
        // 获取锁
        RLock lock = redissonClient.getLock("user-center:user:search:do-ache");
        // 尝试获取锁
        // 为锁添加过期时间
        if (!lock.tryLock(0, 0, TimeUnit.MINUTES)) {
            return;
        }
        log.info("lock user-center:user:search:do-ache");
        try {
            log.info("begin doCacheRecommend");
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("is_delete", false);
            for (int current = 1; current <= 5; current++) {
                String redisKey = String.format("user-center:user:search-%d-%d", current, 5);
                Page<User> userPage = userService.page(new Page<>(current, 5), userQueryWrapper);
                opsForValue.set(redisKey, userPage, 1, TimeUnit.DAYS);
            }
            log.info("end doCacheRecommend");
        } catch (Exception e) {
            log.error("redis set key error", e);
        } finally {
            // 释放锁
            // 判断锁是否被当前线程所持有
            if (lock.isHeldByCurrentThread()) {
                log.info("unlock user-center:user:search:do-ache");
                lock.unlock();
            }
        }
    }
}
