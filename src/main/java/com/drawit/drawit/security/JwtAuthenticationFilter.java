package com.drawit.drawit.security;

import com.drawit.drawit.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        log.info("Handshake Query Parameters: " + request.getRequestURL());
        if (token == null || token.isEmpty()) {
            log.info(request.getHeader("Authorization"));
            log.info("jwt token missing");
            token = request.getParameter("token");
            log.info("query string token: " + token);
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getLoginId(token);
            log.info("user login id: " + username);
            UserDetails userDetails = userService.loadUserByUsername(username);
            log.info("Loaded user: " + userDetails);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), null, userDetails.getAuthorities());

            log.info("JWT Token: " + token);
            log.info("Is token valid? " + jwtTokenProvider.validateToken(token));
            log.info("Username from token: " + username);
            log.info("Loaded user details: " + userDetails);
            log.info("Authorities: " + userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("ok");
        }
        log.info("Authentication before filter chain execution: " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
        log.info("do filter ok");
        log.info("Authentication after filter chain execution: " + SecurityContextHolder.getContext().getAuthentication());

    }

    // 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
