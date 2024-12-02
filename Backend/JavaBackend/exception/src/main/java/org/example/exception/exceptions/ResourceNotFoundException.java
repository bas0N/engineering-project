package org.example.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;
    private final String errorCode;
    private final Map<String, Object> additionalDetails;

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue, String errorCode, Map<String, Object> additionalDetails) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        this(resourceName, fieldName, fieldValue, null, null);
    }
}
