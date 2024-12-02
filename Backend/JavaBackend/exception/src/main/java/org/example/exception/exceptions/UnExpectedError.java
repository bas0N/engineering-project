package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class UnExpectedError extends RuntimeException {
    public UnExpectedError(String message) {
        super(message);
    }

    public UnExpectedError(String message, Throwable cause) {
        super(message, cause);
    }
}
