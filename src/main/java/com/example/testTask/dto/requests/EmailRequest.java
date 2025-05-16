package com.example.testTask.dto.requests;

import lombok.Data;

@Data
public class EmailRequest {
    private String newEmail;
    private String oldEmail;
}
