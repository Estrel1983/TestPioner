package com.example.testTask.service;

import com.example.testTask.dao.PhoneDataDao;
import com.example.testTask.dao.UserDao;
import com.example.testTask.dto.EmailData;
import com.example.testTask.dto.PhoneData;
import com.example.testTask.dto.Users;
import com.example.testTask.util.Validator;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneDataService {
    private final UserDao userDao;
    private final PhoneDataDao phoneDataDao;
    private final UserService userService;

    public PhoneData addPhone(Long userId, String phoneNumber) throws ValidationException {
        if (!Validator.isPhoneValid(phoneNumber))
            throw new ValidationException("Phone number isn't valid");
        Users user = userService.getUserOrThrow(userId);
        PhoneData phone = new PhoneData();
        phone.setPhone(phoneNumber);
        phone.setUser(user);
        try {
            phone = phoneDataDao.save(phone);
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.hibernate.exception.ConstraintViolationException)
                throw new ValidationException("Phone number isn't unique");
            throw e;
        }
        return phone;
    }

    @Transactional
    public boolean deletePhone(Long userId, String phoneNumber) {
        if (!Validator.isPhoneValid(phoneNumber))
            throw new ValidationException("Phone number isn't valid");
        Users user = userService.getUserOrThrow(userId);
        List<PhoneData> phoneDataList = phoneDataDao.findPhonesByUser(user);
        if (phoneDataList.size() > 1) {
            PhoneData curPhone = phoneDataList.stream().filter(phoneData -> phoneNumber.equals(phoneData.getPhone())).findFirst()
                    .orElseThrow(() -> new RuntimeException("Phone isn't found"));
            phoneDataDao.delete(curPhone);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean changePhone(Long userId, String newPhoneNumber, String oldPhoneNumber) {
        return addPhone(userId, newPhoneNumber) != null && deletePhone(userId, oldPhoneNumber);
    }
}
