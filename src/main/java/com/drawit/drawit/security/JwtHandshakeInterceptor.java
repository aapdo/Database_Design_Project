package com.drawit.drawit.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Query Parameter에서 JWT 추출
        String jwtToken = extractJwtFromRequest(request);
        log.info("socket jwt: " + jwtToken);

        // JWT 검증
        if (jwtTokenProvider.validateToken(jwtToken)) {
            // 사용자 정보를 WebSocket 세션 속성에 저장
            attributes.put("userId", jwtTokenProvider.getUserId(jwtToken));
            attributes.put("loginId", jwtTokenProvider.getLoginId(jwtToken));
            attributes.put("nickname", jwtTokenProvider.getNickname(jwtToken));
            return true; // 인증 성공
        } else {
            log.warn("Invalid or missing JWT token");
            return false; // 인증 실패
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("aszss");
    }

    private String extractJwtFromRequest(ServerHttpRequest request) {
        // Query Parameter에서 JWT 추출 (예: ?token=jwt_value)
        String query = request.getURI().getQuery();
        log.info("extractJwt query: " + query);
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1];
        }
        return null; // 토큰이 없는 경우 null 반환
    }

    private boolean isValidJwt(String jwtToken) {
        return true; // 예제에서는 항상 성공으로 가정
    }

    private String extractUserFromJwt(String jwtToken) {
        // JWT에서 사용자 정보 추출 (예: 사용자 ID)
        return "exampleUser";
    }
}
