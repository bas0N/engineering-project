package org.example.exception.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomGlobalExceptionHandler {
    private ResponseEntity<GenericErrorResponse> buildGenericErrorResponse(
            HttpStatus httpStatus,
            String userFriendlyMessage,
            String errorCode
    ) {
        GenericErrorResponse response = new GenericErrorResponse(
                new Date(),
                userFriendlyMessage,
                errorCode
        );
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                        WebRequest webRequest) {
        log.error("Resource not found: {}", ex.getMessage(), ex);

        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "NOT_FOUND";
        Object additionalDetails = ex.getAdditionalDetails();

        log.error("Resource not found: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found. Please check your request.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "NOT_FOUND"
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex, WebRequest webRequest) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = "INTERNAL_SERVER_ERROR";
        Map<String, Object> additionalDetails = Map.of("errorType", ex.getClass().getSimpleName());

        log.error("An unexpected error occurred: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = "ACCESS_DENIED";
        Map<String, Object> additionalDetails = Map.of("hint", "You do not have the necessary permissions to access this resource.");

        log.error("Access denied: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource.",
                "ACCESS_DENIED"
        );
    }


    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<?> handleApiRequestException(ApiRequestException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "BAD_REQUEST";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("API request exception: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad request. Please verify your input data.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "BAD_REQUEST"
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "UNAUTHORIZED";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Unauthorized exception: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "You are not authorized to perform this action.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "UNAUTHORIZED"
        );
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<?> handleInvalidParameterException(InvalidParameterException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "BAD_REQUEST";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Invalid parameter exception: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid input. Please revise your request and try again.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "BAD_REQUEST"
        );
    }

    @ExceptionHandler(DatabaseAccessException.class)
    public ResponseEntity<?> handleDatabaseAccessException(DatabaseAccessException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "DATABASE_ERROR";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Database access exception: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A database error has occurred. Please try again later.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "DATABASE_ERROR"
        );
    }

    @ExceptionHandler(SearchOperationException.class)
    public ResponseEntity<?> handleSearchOperationException(SearchOperationException ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String requestDescription = webRequest.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "SEARCH_ERROR";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Search operation exception: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred during search. Please try again later.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "SEARCH_ERROR"
        );
    }

    @ExceptionHandler(NoBasketInfoException.class)
    public ResponseEntity<?> handleNoBasketInfoException(NoBasketInfoException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "NO_BASKET_INFO";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Basket information is missing or invalid: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Basket information is missing or invalid.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "NO_BASKET_INFO"
        );
    }

    @ExceptionHandler(UnExpectedError.class)
    public ResponseEntity<?> handleUnExpectedError(UnExpectedError ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "UNEXPECTED_ERROR";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("An unexpected internal error occurred: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred. Please try again later.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "UNEXPECTED_ERROR"
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = "INVALID_TOKEN";

        log.error("Invalid token: {}, Error Code: {}, Request Description: {}",
                errorMessage, errorCode, requestDescription);

        return buildGenericErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "The provided token is invalid or expired.",
                "INVALID_TOKEN"
        );
    }
    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<?> handleLikeAlreadyExistsException(LikeAlreadyExistsException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : "LIKE_EXISTS";
        Map<String, Object> additionalDetails = ex.getAdditionalDetails();

        log.error("Like already exists: {}, Error Code: {}, Request Description: {}, Additional Details: {}",
                errorMessage, errorCode, requestDescription, additionalDetails);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "You have already liked this item.",
                ex.getErrorCode() != null ? ex.getErrorCode() : "LIKE_EXISTS"
        );
    }

    @ExceptionHandler(UserIsUnActiveException.class)
    public ResponseEntity<?> handleUserIsUnActiveException(UserIsUnActiveException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = "USER_IS_UNACTIVE";

        log.error("User is inactive: {}, Error Code: {}, Request Description: {}",
                errorMessage, errorCode, requestDescription);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "This user account is inactive. Please contact support.",
                "USER_IS_INACTIVE"
        );
    }

    @ExceptionHandler(ProductIsUnActive.class)
    public ResponseEntity<?> handleProductIsUnActive(ProductIsUnActive ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        String requestDescription = request.getDescription(false);
        String errorCode = "PRODUCT_IS_UNACTIVE";

        log.error("Product is inactive: {}, Error Code: {}, Request Description: {}",
                errorMessage, errorCode, requestDescription);

        return buildGenericErrorResponse(
                HttpStatus.BAD_REQUEST,
                "This product is currently inactive and cannot be purchased.",
                "PRODUCT_IS_INACTIVE"
        );
    }

}
