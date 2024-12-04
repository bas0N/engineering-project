package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class DatabaseAccessException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DATABASE_ACCESS_ERROR";
        this.additionalDetails = null;
    }

    public DatabaseAccessException(String message, Throwable cause, String errorCode, Map<String, Object> additionalDetails) {
        super(message, cause);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
