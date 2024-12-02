package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class SearchOperationException extends RuntimeException {
    public SearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
