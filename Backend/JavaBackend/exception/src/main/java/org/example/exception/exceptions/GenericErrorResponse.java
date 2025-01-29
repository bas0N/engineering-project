package org.example.exception.exceptions;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class GenericErrorResponse {
    private Date timestamp;
    private String message;
    private String errorCode;
}
