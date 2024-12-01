package com.drawit.drawit.configuration;

import com.drawit.drawit.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 수 있는 주제를 설정
        registry.enableSimpleBroker("/game", "/queue");
        // 클라이언트가 메시지를 보낼 때 사용하는 prefix 설정
        registry.setApplicationDestinationPrefixes("/ws");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 서버에 연결할 엔드포인트 설정
        registry.addEndpoint("/ws")
                //.addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider))
                .setAllowedOrigins("http://localhost:3000", "http://localhost:8081", "http://drawit-project-bucket.s3-website.ap-northeast-2.amazonaws.com") // CORS 설정
                .withSockJS(); // SockJS를 통한 fallback 지원

    }
}
