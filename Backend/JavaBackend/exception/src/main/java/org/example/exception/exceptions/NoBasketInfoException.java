package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class NoBasketInfoException extends RuntimeException {
    public NoBasketInfoException(String message) {
        super(message);
    }
    public NoBasketInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
