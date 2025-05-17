package com.example.testTask.dto.response;
import com.example.testTask.dto.Users;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;

    public UserResponse(Users user){
        this.id = user.getId();
        this.name= user.getName();
        this.dateOfBirth=user.getDateOfBirth();
    }
}
