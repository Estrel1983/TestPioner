package com.example.testTask;

import com.example.testTask.dao.AccountDao;
import com.example.testTask.dto.Account;
import com.example.testTask.dto.Users;
import com.example.testTask.dto.requests.TransferRequest;
import com.example.testTask.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountDao accountDao;
    @InjectMocks
    private AccountService accountService;

    private final Long fromUserId = 1L;
    private final Long toUserId = 2L;
    private final BigDecimal initialBalance = new BigDecimal("100.00");
    private final BigDecimal destinBalance = new BigDecimal("50.00");
    private final BigDecimal transferAmount = new BigDecimal("50.00");

    @Test
    void testSuccessfulTransfer_success(){
        Account from = createAccount(initialBalance, fromUserId);
        Account to = createAccount(destinBalance, toUserId);
        TransferRequest request = new TransferRequest();
        request.setDestinationId(toUserId);
        request.setAmount(transferAmount);
        when(accountDao.findAndLockByUserId(fromUserId)).thenReturn(from);
        when(accountDao.findAndLockByUserId(toUserId)).thenReturn(to);

        accountService.transferBalance(fromUserId, request);
        assertEquals(0, from.getBalance().compareTo(new BigDecimal("50.00")));
        assertEquals(0, to.getBalance().compareTo(new BigDecimal("100.00")));
    }
    @Test
    void testTransferToSelf_throw(){
        TransferRequest request = new TransferRequest();
        request.setDestinationId(fromUserId);
        request.setAmount(transferAmount);
        assertThrows(IllegalArgumentException.class, ()->accountService.transferBalance(fromUserId, request));
    }

    @Test
    void testInsufficientAmount_throw(){
        TransferRequest request = new TransferRequest();
        request.setDestinationId(toUserId);
        request.setAmount(transferAmount);
        Account from = createAccount(new BigDecimal(10), fromUserId);
        Account to = createAccount(destinBalance, toUserId);

        when(accountDao.findAndLockByUserId(fromUserId)).thenReturn(from);
        when(accountDao.findAndLockByUserId(toUserId)).thenReturn(to);

        assertThrows(IllegalArgumentException.class, ()->accountService.transferBalance(fromUserId, request));
    }

    @Test
    void testInvalidAccountsShould_throw() {
        TransferRequest request = new TransferRequest();
        request.setDestinationId(toUserId);
        request.setAmount(transferAmount);

        when(accountDao.findAndLockByUserId(fromUserId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                accountService.transferBalance(fromUserId, request));
    }
    private Account createAccount(BigDecimal balance, Long userId){
        Account result = new Account();
        result.setBalance(balance);
        Users user = new Users();
        user.setId(userId);
        user.setName("User"+userId);
        result.setUser(user);
        return result;
    }

}
