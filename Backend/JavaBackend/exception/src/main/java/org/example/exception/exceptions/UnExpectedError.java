package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class UnExpectedError extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public UnExpectedError(String message) {
        super(message);
        this.errorCode = "UNEXPECTED_ERROR";
        this.additionalDetails = null;
    }

    public UnExpectedError(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }

    public UnExpectedError(String message, Throwable cause, String errorCode, Map<String, Object> additionalDetails) {
        super(message, cause);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
