package org.example.commonutils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Utils {
    public String extractUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("userId");

        if (userId == null || userId.isEmpty()) {
            log.warn("UserId header not found or is empty in the request");
            return null;
        }

        log.info("Extracted userId from request: {}", userId);
        return userId;
    }

    public String extractTokenFromRequest(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            log.warn("Authorization header not found or is empty in the request");
            return null;
        }

        log.info("Extracted token from request: {}", token);
        return token;
    }
}
