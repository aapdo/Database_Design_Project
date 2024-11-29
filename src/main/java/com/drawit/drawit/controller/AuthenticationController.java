package com.drawit.drawit.controller;

import com.drawit.drawit.dto.AuthenticationPrincipalDto;
import com.drawit.drawit.dto.JwtLoginDto;
import com.drawit.drawit.dto.UserDto;
import com.drawit.drawit.dto.request.RequestLoginDto;
import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.security.JwtTokenProvider;
import com.drawit.drawit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RequestRegisterDto requestRegisterDto) {
        log.info("try register");
        // 사용자 존재 여부 확인
        log.info(requestRegisterDto.getLoginId());
        if (userService.isLoginIdDuplicate(requestRegisterDto.getLoginId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated id");
        }
        if (userService.isNicknameDuplicate(requestRegisterDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated nickname");
        }

        // 사용자 생성
        User user = User.builder()
                .loginId(requestRegisterDto.getLoginId())
                .password(passwordEncoder.encode(requestRegisterDto.getPassword()))
                .nickname(requestRegisterDto.getNickname())
                .totalPoints(200)
                .currentPoints(200)
                .createdAt(LocalDateTime.now())
                // 기타 필드 초기화
                .build();

        log.info("register user = " + user);

        userService.register(user);




        return ResponseEntity.ok("User registered successfully");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody RequestLoginDto requestLoginDto, HttpServletResponse response) {
        log.info("try login");
        try {
             authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDto.getLoginId(),
                            requestLoginDto.getPassword()
                    )
            );
            log.info("Authenticating user with loginId: " + requestLoginDto.getLoginId());

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDto userDto;
        try {
            userDto = userService.getUserByLoginId(requestLoginDto.getLoginId());
            log.info("login user: " + userDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("login failed");
        }


        String token = jwtTokenProvider.createToken(userDto.getId(), userDto.getLoginId(), userDto.getNickname());

        JwtLoginDto jwtLoginDto = JwtLoginDto.builder()
                .accessToken(token)
                .loginId(requestLoginDto.getLoginId())
                .userId((userDto.getId()))
                .nickname(userDto.getNickname())
                .build();

        HttpHeaders header = new HttpHeaders();
        Cookie loginCookie = new Cookie("loginToken", jwtLoginDto.getAccessToken());
        Cookie nicknameCookie = new Cookie("userNickname", userDto.getNickname());


        loginCookie.setPath("/");
        // 30일 간 쿠키 유지.
        loginCookie.setMaxAge(60 * 60 * 24 * 30);

        header.add("Set-Cookie", loginCookie.toString());
        header.add("Authorization", "Bearer "+jwtLoginDto.getAccessToken());
        header.add("Set-Cookie", nicknameCookie.toString());

        response.addCookie(loginCookie);
        log.info("suc login");

        return ResponseEntity.ok().headers(header).body(jwtLoginDto);
    }



    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }
}
