package org.example.message.interceptor;

import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtCommonService jwtCommonService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //String jwtToken = jwtCommonService.getTokenFromRequest(request);
        String jwtToken = jwtCommonService.getTokenFromRequestServer(request);
        if (jwtToken != null && jwtCommonService.validateToken(jwtToken)) {
            String userId = jwtCommonService.getCurrentUserId(jwtToken);
            attributes.put("userId", userId);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
