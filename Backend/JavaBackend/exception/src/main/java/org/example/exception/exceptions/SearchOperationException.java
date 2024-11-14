package org.example.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SearchOperationException extends RuntimeException {
    public SearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
