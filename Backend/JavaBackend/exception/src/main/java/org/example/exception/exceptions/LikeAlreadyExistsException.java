package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class LikeAlreadyExistsException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public LikeAlreadyExistsException(String message, Map<String, Object> additionalDetails) {
        super(message);
        this.additionalDetails = additionalDetails;
        this.errorCode = "LIKE_EXISTS";
    }

    public LikeAlreadyExistsException(String message) {
        super(message);
        this.additionalDetails = Map.of();
        this.errorCode = "LIKE_EXISTS";
    }

}
