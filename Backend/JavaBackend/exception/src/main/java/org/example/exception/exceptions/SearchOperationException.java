package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class SearchOperationException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public SearchOperationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SEARCH_OPERATION_ERROR";
        this.additionalDetails = null;
    }

    public SearchOperationException(String message, Throwable cause, String errorCode, Map<String, Object> additionalDetails) {
        super(message, cause);
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
