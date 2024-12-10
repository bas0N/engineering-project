package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class ApiRequestException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public ApiRequestException(String message) {
        super(message);
        this.errorCode = "BAD_REQUEST";
        this.additionalDetails = null;
    }

    public ApiRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = null;
    }

    public ApiRequestException(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }

    public ApiRequestException(String message, Throwable cause, String errorCode, Map<String, Object> additionalDetails) {
        super(message, cause);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
