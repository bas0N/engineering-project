package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class LikeAlreadyExistsException extends RuntimeException {
    private final String errorCode;

    public LikeAlreadyExistsException(String message) {
        super(message);
        this.errorCode = "LIKE_EXISTS";
    }

    public LikeAlreadyExistsException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
