package com.example.testTask.api;

import com.example.testTask.dto.requests.TransferRequest;
import com.example.testTask.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Tag(name = "Balance", description = "Balance modification operations")
public class AccountController {
    private final AccountService as;

    @PostMapping("/transfer")
    @Operation(summary = "Money transferring to current user")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            as.transferBalance(userId, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (PessimisticLockException | LockTimeoutException ei) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account is temporarily locked. Try again.");
        }
    }
}
