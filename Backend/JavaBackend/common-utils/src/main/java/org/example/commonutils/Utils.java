package org.example.commonutils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@RequiredArgsConstructor
@Slf4j
public class Utils {
    @Value("${jwt.secret}") String SECRET;
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

    private String getClaimFromToken(String token, String claimKey) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserIdFromToken(String jwtToken) {
        return getClaimFromToken(jwtToken, "uuid");
    }
}
