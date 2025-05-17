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

    @Scheduled(fixedRateString = "${balance.scheduler.fixedRate}")
    public void autoUpdateBalance(){
        as.applyBalanceGrowth();
    }
}
