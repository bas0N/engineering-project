package org.example.auth.dto.response;

import lombok.Getter;
import org.example.auth.entity.Code;

import java.sql.Timestamp;

@Getter
public class AuthResponse {
    private final String timestamp;
    private final String message;
    private final Code code;
    private final String token;
    private final String refreshToken;

    public AuthResponse(Code code, String token, String refreshToken) {
        this.timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        this.message = code.label;
        this.code = code;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
