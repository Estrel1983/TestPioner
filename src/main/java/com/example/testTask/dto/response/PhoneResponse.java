package com.example.testTask.dto.response;

import com.example.testTask.dto.PhoneData;
import lombok.Data;

@Data
public class PhoneResponse {
    private Long id;
    private String phoneNumber;
    private String userName;
    public PhoneResponse(PhoneData pd){
        this.id = pd.getId();
        this.phoneNumber = pd.getPhone();
        this.userName = pd.getUser().getName();
    }
}
