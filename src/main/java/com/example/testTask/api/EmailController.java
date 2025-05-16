package com.example.testTask.api;

import com.example.testTask.dto.*;
import com.example.testTask.dto.requests.EmailRequest;
import com.example.testTask.service.EmailService;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService es;
    @PostMapping
    public ResponseEntity<?> addPhone(@RequestBody EmailRequest er){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EmailData newEmail = es.addEmail(userId, er.getNewEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(newEmail);
        } catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletePhone (@RequestBody EmailRequest er){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (es.deleteEmail(userId, er.getOldEmail()))
                return ResponseEntity.noContent().build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete the only email.");
        }catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }
    @PatchMapping
    public ResponseEntity<?> changePhone(@RequestBody EmailRequest er) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean changed = es.changeEmail(userId, er.getNewEmail(), er.getOldEmail());
            if (changed) {
                return ResponseEntity.ok("Email changed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Only one email exists, cannot delete");
            }
        } catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }
}
