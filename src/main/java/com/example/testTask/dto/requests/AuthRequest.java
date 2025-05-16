package com.example.testTask.dto.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String phone;
    private String password;
}
