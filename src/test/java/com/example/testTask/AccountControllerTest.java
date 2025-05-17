package com.example.testTask;

import com.example.testTask.api.AccountController;
import com.example.testTask.service.AccountService;
import com.example.testTask.service.JwtService;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    void testTransfer_success() throws Exception{
        TestingAuthenticationToken auth = new TestingAuthenticationToken(1L, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "destinationId": 2,
                            "amount": 50.00
                        }
                        """)
                .with(csrf()))

                .andExpect(status().isOk());
        verify(accountService).transferBalance(eq(1L), any());
    }
    @Test
    void testTransfer_throwsIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("Bad request"))
                .when(accountService)
                .transferBalance(eq(1L), any());

        TestingAuthenticationToken auth = new TestingAuthenticationToken(1L, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "destinationId": 2,
                        "amount": 50.00
                    }
                    """)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    @Test
    void testTransferThrowsLockException() throws Exception {
        doThrow(new PessimisticLockException())
                .when(accountService)
                .transferBalance(eq(1L), any());
        TestingAuthenticationToken auth = new TestingAuthenticationToken(1L, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
        mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "destinationId": 2,
                        "amount": 50.00
                    }
                    """)
                .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Account is temporarily locked. Try again."));
    }

}
