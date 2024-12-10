package org.example.exception.exceptions;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
    private String errorCode;
    private Map<String, Object> additionalDetails;

    public ErrorDetails(Date timestamp, String message, String details, String errorCode, Map<String, Object> additionalDetails) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.errorCode = errorCode;
        this.additionalDetails = additionalDetails;
    }
}
