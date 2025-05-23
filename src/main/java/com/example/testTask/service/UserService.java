package com.example.testTask.service;

import com.example.testTask.dao.EmailDao;
import com.example.testTask.dao.PhoneDataDao;
import com.example.testTask.dao.UserDao;
import com.example.testTask.dto.UserSearchFilter;
import com.example.testTask.dto.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final PhoneDataDao phoneDao;
    private final EmailDao emailDao;

    public List<Users> searchUsers(UserSearchFilter usf) {
        Users curUser = null;
        if (usf.email() != null)
            curUser = emailDao.findUserByEmail(usf.email()).orElse(null);
        if (usf.phone() != null) {
            Users checkingUser = phoneDao.findUserByPhone(usf.phone()).orElse(null);
            if (curUser != null && (checkingUser != null && !curUser.getId().equals(checkingUser.getId())))
                return Collections.emptyList();
            curUser = checkingUser;
        }
        List<Users> curUseersList;
        if (usf.name() != null || usf.dateOfBirth() != null) {
            curUseersList = userDao.findUser(usf.name(), usf.dateOfBirth(), usf.page(), usf.size());
            if (curUser == null)
                return curUseersList;
            Users finalCurUser = curUser;
            if (curUseersList.stream().anyMatch(user -> user.getId().equals(finalCurUser.getId())))
                return List.of(curUser);
            return Collections.emptyList();
        } else if (curUser != null)
            return List.of(curUser);
        return Collections.emptyList();
    }

    public Users getUserOrThrow(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
    }
}
