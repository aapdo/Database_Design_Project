package com.drawit.drawit.controller;

import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.dto.response.ResponseUserDto;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserList() {
        List<ResponseUserDto> userList = new ArrayList<>();
        List<User> users = userService.getUserList();
        int userSize = users.size();

        for (int i = 0; i < userSize; i++) {
            userList.add(new ResponseUserDto(users.get(i)));
        }

        return ResponseEntity.ok(userList);
    }
}
