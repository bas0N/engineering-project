package org.example.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingUserDetailsException extends RuntimeException{
    public MissingUserDetailsException(String message) {
        super(message);
    }
}
