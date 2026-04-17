package com.example.gacapp.exception;

import com.example.gacapp.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= VALIDATION =================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
                ApiResponse.error("Validation failed", "VAL_001", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                ApiResponse.error("Malformed JSON request or invalid field value", "VAL_002", null),
                HttpStatus.BAD_REQUEST
        );
    }

    // ================= AUTH / USER =================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_002", null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_001", null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleLoginFailedException(LoginFailedException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_005", null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenExpiredException(TokenExpiredException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_003", null),
                HttpStatus.UNAUTHORIZED
        );
    }

    // ================= REGISTRATION =================

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyExistsException(EmailAlreadyExistException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "REG_001", null),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleRegistrationFailedException(RegistrationFailedException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "REG_002", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ================= APPROVAL =================

    @ExceptionHandler(ApprovalRequiredException.class)
    public ResponseEntity<ApiResponse<String>> handleApprovalRequiredException(ApprovalRequiredException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_004", null),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ApprovalRejectionException.class)
    public ResponseEntity<ApiResponse<String>> handleApprovalRejectionException(ApprovalRejectionException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_006", null),
                HttpStatus.FORBIDDEN
        );
    }

    // ================= EMAIL =================

    @ExceptionHandler(FailedToSendPasswordResetEmailException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailFailure(FailedToSendPasswordResetEmailException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "EMAIL_001", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ================= GENERAL =================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "RES_001", null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(
                ApiResponse.error("An unexpected error occurred", "SYS_001", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}