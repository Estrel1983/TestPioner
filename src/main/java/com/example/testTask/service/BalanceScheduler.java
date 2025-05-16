package com.example.testTask.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BalanceScheduler {
    private final AccountService as;

    @Scheduled(fixedRate = 30000)
    public void autoUpdateBalance(){
        as.applyBalanceGrowth();
    }
}
