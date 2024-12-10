package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class MissingUserDetailsException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public MissingUserDetailsException(String message) {
        super(message);
        this.errorCode = "MISSING_USER_DETAILS";
        this.additionalDetails = null;
    }

    public MissingUserDetailsException(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
