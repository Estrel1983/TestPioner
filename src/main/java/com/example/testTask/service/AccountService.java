package com.example.testTask.service;

import com.example.testTask.dao.AccountDao;
import com.example.testTask.dto.Account;
import com.example.testTask.dto.requests.TransferRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {
    private final AccountUpdater updater;
    private final AccountDao accountDao;

    public void applyBalanceGrowth(){
        List<Account> accounts = accountDao.findAll();
        for (Account acc : accounts){
            try{
                updater.updateOneAccount(acc);
            } catch (Exception e) {
                //Logger?
            }
        }
    }
    @Transactional
    public void transferBalance(Long fromUserId, TransferRequest request) {
        if (fromUserId.equals(request.getDestinationId()))
            throw new IllegalArgumentException("Cannot transfer to yourself");
        Account fromAccount = accountDao.findAndLockByUserId(fromUserId);
        Account toAccount = accountDao.findAndLockByUserId(request.getDestinationId());
        if (fromAccount == null || toAccount == null)
            throw new IllegalArgumentException("Wrong accounts for transferring");
        BigDecimal amount = request.getAmount();
        if (fromAccount.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient balance");
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountDao.updateAccount(fromAccount);
        accountDao.updateAccount(toAccount);
    }
}
