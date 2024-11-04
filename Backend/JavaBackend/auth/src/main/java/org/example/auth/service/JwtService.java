package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface JwtService {
    void validateToken(final String token);

    String generateToken(String email, String uuid, int exp);

    String createToken(Map<String, Object> claims, String subject, int exp);

    String getSubject(final String token);

    String refreshToken(final String token, int exp);

    String getEmailFromToken(String token);

    String getUuidFromToken(String token);

    String getTokenFromRequest(HttpServletRequest request);

    String getUuidFromRequest(HttpServletRequest request);

}
