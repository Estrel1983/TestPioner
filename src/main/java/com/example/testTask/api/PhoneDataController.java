package com.example.testTask.api;

import com.example.testTask.dto.PhoneData;
import com.example.testTask.dto.requests.PhoneRequest;
import com.example.testTask.service.PhoneDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/phone")
@AllArgsConstructor
@Tag(name = "Phones", description = "User phone number management")
public class PhoneDataController {
    private final PhoneDataService pds;
    @PostMapping
    @Operation(summary = "Add phone number")
    public ResponseEntity<?> addPhone(@RequestBody PhoneRequest pr){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            PhoneData newPhone = pds.addPhone(userId, pr.getNewPhone());
            return ResponseEntity.status(HttpStatus.CREATED).body(newPhone);
        } catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete phone number. (You can't delete the only number)")
    public ResponseEntity<?> deletePhone (@RequestBody PhoneRequest pr){
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (pds.deletePhone(userId, pr.getOldPhone()))
                return ResponseEntity.noContent().build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete the only phone number.");
        }catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }
    @PatchMapping
    @Operation(summary = "Change phone number")
    public ResponseEntity<?> changePhone(@RequestBody PhoneRequest pr) {
        try {
            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean changed = pds.changePhone(userId, pr.getNewPhone(), pr.getOldPhone());
            if (changed) {
                return ResponseEntity.ok("Phone changed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Only one phone number exists, cannot delete");
            }
        } catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re.getMessage());
        }
    }
}
