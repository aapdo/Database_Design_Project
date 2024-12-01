package com.drawit.drawit.controller;

import com.drawit.drawit.dto.UserDto;
import com.drawit.drawit.dto.UserListDto;
import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.dto.response.ResponseUserDto;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.service.HttpRequestService;
import com.drawit.drawit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final HttpRequestService httpRequestService;
    @Value("${python.server.base-url}")
    private String pythonBaseUrl; // application.properties에서 값 주입

    @GetMapping("/test")
    public String test() {
        String ret = httpRequestService.sendGetRequest(pythonBaseUrl + "/getSimilarity?correctWord=apple&guessedWord=orange");
        log.info("test flask" + ret);
        return ret;
    }

    @GetMapping
    public ResponseEntity<?> getUserList() {
        Long userId =  Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<UserListDto> users = userService.getUserList(userId);


        return ResponseEntity.ok(users);
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<?> getUserProfileByNickname(@PathVariable("nickname") String nickname) {
        ResponseUserDto user;
        try {
             user = new ResponseUserDto(userService.getUserByNickname(nickname));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not found.");
        }
        return ResponseEntity.ok(user);
    }
    @PostMapping("/update/{nickname}")
    public ResponseEntity<?> updateUserNickname(@PathVariable("nickname") String nickname) {
        // 닉네임 이미 사용중이면 400 에러
        if (userService.isNicknameDuplicate(nickname)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated nickname");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // getname 을 사용하면 user id를 얻을 수 있다.
        log.info("user id: " + authentication.getName());
        log.info("user : " + Long.parseLong(authentication.getName().trim()));
        Long userId = Long.parseLong(authentication.getName().trim());

        try {
            userService.updateUserNickName(userId, nickname);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 접근");
        }
        return ResponseEntity.ok("success");
    }

}
