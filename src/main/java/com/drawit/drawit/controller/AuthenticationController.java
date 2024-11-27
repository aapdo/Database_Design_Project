package com.drawit.drawit.controller;

import com.drawit.drawit.dto.JwtLoginDto;
import com.drawit.drawit.dto.request.RequestLoginDto;
import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.security.JwtTokenProvider;
import com.drawit.drawit.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
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
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RequestRegisterDto requestRegisterDto) {
        // 사용자 존재 여부 확인
        log.info(requestRegisterDto.getLoginId());
        if (customUserDetailsService.isLoginIdDuplicate(requestRegisterDto.getLoginId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated id");
        }
        if (customUserDetailsService.isNicknameDuplicate(requestRegisterDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated nickname");
        }

        // 사용자 생성
        User user = User.builder()
                .loginId(requestRegisterDto.getLoginId())
                .password(passwordEncoder.encode(requestRegisterDto.getPassword()))
                .nickname(requestRegisterDto.getNickname())
                .createdAt(LocalDateTime.now())
                // 기타 필드 초기화
                .build();

        customUserDetailsService.saveUser(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody RequestLoginDto requestLoginDto, HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDto.getLoginId(),
                            requestLoginDto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Optional<User> optionalUser = customUserDetailsService.getUserByLoginId(requestLoginDto.getLoginId());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
        }
        User user = optionalUser.get();


        String token = jwtTokenProvider.createToken(user.getLoginId());

        JwtLoginDto jwtLoginDto = JwtLoginDto.builder()
                .accessToken(token)
                .loginId(requestLoginDto.getLoginId())
                .userId((user.getId()))
                .nickname(user.getNickname())
                .build();

        HttpHeaders header = new HttpHeaders();
        Cookie loginCookie = new Cookie("loginToken", jwtLoginDto.getAccessToken());

        loginCookie.setPath("/");
        // 30일 간 쿠키 유지.
        loginCookie.setMaxAge(60 * 60 * 24 * 30);

        header.add("Set-Cookie", loginCookie.toString());
        header.add("Authorization", "Bearer "+jwtLoginDto.getAccessToken());
        response.addCookie(loginCookie);

        return ResponseEntity.ok().headers(header).body(jwtLoginDto);
    }



    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }
}
