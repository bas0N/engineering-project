package org.example.message.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonutils.Utils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final Utils utils;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        log.debug("Intercepting handshake: Headers = {}", request.getHeaders());
        String jwtToken = request.getURI().getQuery().split("token=")[1];
        if (jwtToken == null) {
            log.warn("No JWT token found in request headers - handshake refused");
            return false;
        }
        String userId = utils.extractUserIdFromToken(jwtToken);
        attributes.put("userId", userId);

        log.debug("Extracted JWT Token: {}", jwtToken);
        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        } else {
            String clientIp = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().toString()
                    : "UNKNOWN_IP";
            log.info("WebSocket handshake successful for client IP: {}", clientIp);
        }
    }
}
