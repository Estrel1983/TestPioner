package com.example.testTask.api;

import com.example.testTask.dto.UserSearchFilter;
import com.example.testTask.dto.Users;
import com.example.testTask.dto.response.UserResponse;
import com.example.testTask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "User services", description = "Users info")
public class UserController {
    private final UserService us;
    private final RedisTemplate<String, List<Users>> redisTemplate;

    @GetMapping("/search")
    @Operation(summary = "Searching information about existing users")
    public ResponseEntity<?> searchUsers(@ModelAttribute UserSearchFilter filter) {
        List<Users> resultList = redisTemplate.opsForValue().get(filter.toString());
        if (resultList == null) {
            resultList = us.searchUsers(filter);
            redisTemplate.opsForValue().set(filter.toString(), resultList);
        }
        if (resultList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resultList.stream().map(UserResponse::new).toList());
    }
}
