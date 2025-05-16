package com.example.testTask.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserSearchFilter {
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private int page;
    private int size;
}
