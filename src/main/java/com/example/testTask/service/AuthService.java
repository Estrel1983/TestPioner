package com.example.testTask.service;

import com.example.testTask.dao.EmailDao;
import com.example.testTask.dao.PhoneDataDao;
import com.example.testTask.dao.UserDao;
import com.example.testTask.dto.Users;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final EmailDao emailDao;
    private final PhoneDataDao phoneDataDao;
    public Optional<Users> authenticate(String email, String phone, String password){
        Optional<Users> curUser = Optional.empty();
        if (email != null)
            curUser = emailDao.findUserByEmail(email);
        if (curUser.isEmpty() && phone != null)
            curUser = phoneDataDao.findUserByPhone(phone);
        if (curUser.isPresent() && password.equals(curUser.get().getPassword()))
            return curUser;
        return Optional.empty();
    }
}
