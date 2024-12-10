package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class NoBasketInfoException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public NoBasketInfoException(String message) {
        super(message);
        this.errorCode = "NO_BASKET_INFO";
        this.additionalDetails = null;
    }

    public NoBasketInfoException(String message, String errorCode, Map<String, Object> additionalDetails) {
        super(message);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
