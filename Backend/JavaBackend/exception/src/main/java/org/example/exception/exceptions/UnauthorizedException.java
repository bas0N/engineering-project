package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
        this.additionalDetails = null;
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = null;
    }

    public UnauthorizedException(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }



}
