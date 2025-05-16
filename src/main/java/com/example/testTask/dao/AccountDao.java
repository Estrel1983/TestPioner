package com.example.testTask.dao;

import com.example.testTask.dto.Account;
import com.example.testTask.dto.Users;
import com.example.testTask.util.Queries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AccountDao {
    @PersistenceContext
    private EntityManager em;
    public List<Account> findAll(){
        return em.createQuery(Queries.GET_ALL_ACCOUNTS, Account.class).getResultList();
    }
    public void updateBalance(Long accountId, BigDecimal newBalance){
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setBalance(newBalance);
            em.merge(account);
        }
    }
    public Account findAndLockByUserId (Long userId){
        return em.createQuery(Queries.GET_ACCOUNT_BY_USER_ID, Account.class).setParameter("userId", userId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
    }
    public void updateAccount(Account account){
        em.merge(account);
    }
}
