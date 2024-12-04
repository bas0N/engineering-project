package org.example.exception.exceptions;

import java.util.Map;

public class UserIsUnActiveException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public UserIsUnActiveException(String message, Map<String, Object> additionalDetails) {
        super(message);
        this.additionalDetails = additionalDetails;
        this.errorCode = "LIKE_EXISTS";
    }

    public UserIsUnActiveException(String message) {
        super(message);
        this.additionalDetails = Map.of();
        this.errorCode = "LIKE_EXISTS";
    }
}
