package com.example.testTask.api;

import com.example.testTask.dao.UserDao;
import com.example.testTask.dto.Users;
import com.example.testTask.dto.requests.AuthRequest;
import com.example.testTask.dto.response.AuthResponse;
import com.example.testTask.service.AuthService;
import com.example.testTask.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService as;
    private final JwtService js;
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        Optional<Users> userOpt = as.authenticate(request.getEmail(), request.getPhone(), request.getPassword());

        if (userOpt.isPresent()) {
            String token = js.generateToken(userOpt.get().getId());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
