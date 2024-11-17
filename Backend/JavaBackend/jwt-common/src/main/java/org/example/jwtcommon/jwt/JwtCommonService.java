package org.example.jwtcommon.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.exception.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.security.Key;
import java.util.List;

@Service
public class JwtCommonService {
    public JwtCommonService(@Value("${jwt.secret}") String secret) {
        SECRET = secret;
    }

    public final String SECRET;

    public String getCurrentUserId(String token) {
        return getClaimFromToken(token, "uuid");
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public String getUserFromRequest(HttpServletRequest request){
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new InvalidTokenException("Token not found");
        }
        return getCurrentUserId(token);
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

}
