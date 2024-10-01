package org.example.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    public JwtService(@Value("${jwt.secret}") String secret) {
        SECRET = secret;
    }

    public final String SECRET;

    public void validateToken(final String token) throws ExpiredJwtException, IllegalArgumentException {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String uuid, int exp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("uuid", uuid);
        return createToken(claims, email, exp);
    }

    public String createToken(Map<String, Object> claims, String subject, int exp) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String getSubject(final String token) {
        return Jwts
                .parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String refreshToken(final String token, int exp) {
        String email = getSubject(token);
        String uuid = getUuidFromToken(token);
        return generateToken(email, uuid, exp);
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, "email");
    }

    // Pobieranie uuid z tokena
    public String getUuidFromToken(String token) {
        return getClaimFromToken(token, "uuid");
    }

    private String getClaimFromToken(String token, String claimKey) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey);
    }
}
