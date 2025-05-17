package com.example.testTask.dto.response;

import com.example.testTask.dto.EmailData;
import lombok.Data;

@Data
public class EmailResponse {
    private Long id;
    private String email;
    private String userName;
    public EmailResponse(EmailData emailData){
        this.id= emailData.getId();
        this.email = emailData.getEmail();
        this.userName = emailData.getUser().getName();
    }
}
