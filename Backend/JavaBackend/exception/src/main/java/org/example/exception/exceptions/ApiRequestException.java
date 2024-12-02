package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class ApiRequestException extends RuntimeException {
    private final String errorCode;

    public ApiRequestException(String message) {
        super(message);
        this.errorCode = "BAD_REQUEST";
    }

    public ApiRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiRequestException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
