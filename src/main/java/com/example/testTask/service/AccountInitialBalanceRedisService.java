package com.example.testTask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class AccountInitialBalanceRedisService {
    private static final String REDIS_KEY = "account:initial-balance";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveIfAbsent(Long accountId, BigDecimal value) {
        if (!exists(accountId)) {
            redisTemplate.opsForHash().put(REDIS_KEY, accountId.toString(), value.toPlainString());
        }
    }

    public BigDecimal get(Long accountId) {
        Object value = redisTemplate.opsForHash().get(REDIS_KEY, accountId.toString());
        return value != null ? new BigDecimal(value.toString()) : null;
    }

    public boolean exists(Long accountId) {
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(REDIS_KEY, accountId.toString()));
    }
}
