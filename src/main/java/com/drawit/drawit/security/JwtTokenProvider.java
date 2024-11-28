package com.drawit.drawit.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // 토큰 유효 시간 (1시간)
    private final long validityInMilliseconds = 3600000;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(Long userId, String loginId, String nickname) {

        Claims claims = Jwts.claims().setSubject(userId.toString()); // 주로 userId를 subject로 설정
        claims.put("nickname", nickname); // nickname 추가
        claims.put("loginId", loginId); // loginId 추가

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 사용자 loginId 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getLoginId(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody().get("loginId");
    }

    public String getNickname(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody().get("nickname");
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key)
                    .build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
