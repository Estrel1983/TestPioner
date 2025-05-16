package com.example.testTask.service;

import com.example.testTask.dao.EmailDao;
import com.example.testTask.dao.UserDao;
import com.example.testTask.dto.EmailData;
import com.example.testTask.dto.Users;
import com.example.testTask.util.Validator;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailService {
    private final UserDao userDao;
    private final EmailDao emailDao;
    private final UserService userService;

    public EmailData addEmail(Long userId, String email) {
        if (!Validator.isEmail(email))
            throw new ValidationException("Email isn't valid");
        Users user = userService.getUserOrThrow(userId);
        EmailData emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(email);
        try {
            emailData = emailDao.save(emailData);
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.hibernate.exception.ConstraintViolationException)
                throw new ValidationException("Email isn't unique");
            throw e;
        }
        return emailData;
    }

    @Transactional
    public boolean deleteEmail(Long userId, String email) {
        if (!Validator.isEmail(email))
            throw new ValidationException("Email isn't valid");
        Users user = userService.getUserOrThrow(userId);
        List<EmailData> emailDataList = emailDao.findEmailsByUser(user);
        if (emailDataList.size() > 1) {
            EmailData curEmail = emailDataList.stream().filter(emailData -> email.equals(emailData.getEmail())).findFirst()
                    .orElseThrow(() -> new RuntimeException("Email isn't found"));
            emailDao.delete(curEmail);
            return true;
        }
        return false;
    }
    @Transactional
    public boolean changeEmail(Long userId, String newEmail, String oldEmail) {
        return addEmail(userId, newEmail) != null && deleteEmail(userId, oldEmail);
    }
}
