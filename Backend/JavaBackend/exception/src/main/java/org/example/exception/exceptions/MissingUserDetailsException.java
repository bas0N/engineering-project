package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class MissingUserDetailsException extends RuntimeException{
    public MissingUserDetailsException(String message) {
        super(message);
    }
}
