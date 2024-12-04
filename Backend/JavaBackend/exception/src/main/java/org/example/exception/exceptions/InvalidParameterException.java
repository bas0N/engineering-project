package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class InvalidParameterException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public InvalidParameterException(String message) {
        super(message);
        this.errorCode = "INVALID_PARAMETER";
        this.additionalDetails = null;
    }

    public InvalidParameterException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = null;
    }

    public InvalidParameterException(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
