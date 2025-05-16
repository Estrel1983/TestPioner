package com.example.testTask.service;

import com.example.testTask.dao.AccountDao;
import com.example.testTask.dto.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class AccountUpdater {
    private final AccountDao accountDao;
    private final AccountInitialBalanceRedisService redisService;
    @Transactional
    public void updateOneAccount(Account account){
        BigDecimal current = account.getBalance();
        redisService.saveIfAbsent(account.getId(), current);
        BigDecimal initial = redisService.get(account.getId());
        BigDecimal maxAllowed = initial.multiply(BigDecimal.valueOf(2.07));
        BigDecimal increased = current.multiply(BigDecimal.valueOf(1.10));
        BigDecimal newBalance = increased.compareTo(maxAllowed) > 0 ? maxAllowed : increased;
        if (newBalance.compareTo(current) > 0)
            accountDao.updateBalance(account.getId(), newBalance);
    }
}
