package org.example.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoBasketInfoException extends RuntimeException {
    public NoBasketInfoException(String message) {
        super(message);
    }
    public NoBasketInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
