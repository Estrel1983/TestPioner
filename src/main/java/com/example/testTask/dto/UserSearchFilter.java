package com.example.testTask.dto;

import java.time.LocalDate;
public record UserSearchFilter(String name,
                               String email,
                               String phone,
                               LocalDate dateOfBirth,
                               Integer page,
                               Integer size) {
    public UserSearchFilter {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }
    }
    @Override
    public String toString(){
        return "%s|%s|%s|%s|%d|%d".formatted(
                name == null ? "":name,
                email == null ? "":email,
                phone == null ? "":phone,
                dateOfBirth == null ? "":dateOfBirth,
                page, size
        );
    }
}
