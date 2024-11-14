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
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    return token;
                }
            }
        }
        return token;
    }

    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Refresh".equals(cookie.getName())) {
                    token = cookie.getValue();
                    return token;
                }
            }
        }
        return token;
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

    public String getTokenFromRequestServer(ServerHttpRequest request) {
        List<String> cookies = request.getHeaders().get(HttpHeaders.COOKIE);
        if (cookies != null) {
            for (String cookieHeader : cookies) {
                for (HttpCookie cookie : HttpCookie.parse(cookieHeader)) {
                    if ("Authorization".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
