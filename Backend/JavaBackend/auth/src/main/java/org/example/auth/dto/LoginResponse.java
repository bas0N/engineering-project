package org.example.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.auth.entity.Code;

import java.sql.Timestamp;

@Getter
@Setter
public class LoginResponse {
    private final String timestamp;
    private final boolean message;
    private final Code code;

    public LoginResponse(boolean message){
        this.timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        this.message = message;
        this.code = Code.SUCCESS;
    }
}
