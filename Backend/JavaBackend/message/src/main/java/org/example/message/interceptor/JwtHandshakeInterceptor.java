package org.example.message.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtCommonService jwtCommonService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        log.debug("Intercepting handshake: Headers = {}", request.getHeaders());
        String jwtToken = jwtCommonService.getTokenFromRequestServer(request);
        log.debug("Extracted JWT Token: {}", jwtToken);
        if (jwtToken != null && jwtCommonService.validateToken(jwtToken)) {
            String userId = jwtCommonService.getCurrentUserId(jwtToken);
            attributes.put("userId", userId);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,@NonNull ServerHttpResponse response,@NonNull WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        } else {
            String clientIp = request.getRemoteAddress().toString();
            log.info("WebSocket handshake successful for client IP: {}", clientIp);
        }
    }
}
