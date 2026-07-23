package com.example.gacapp.exception;

import com.example.gacapp.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ================= VALIDATION =================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed for request: {}", errors);

        return new ResponseEntity<>(
                ApiResponse.error("Validation failed", "VAL_001", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        log.warn("Malformed JSON or unreadable message: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error("Malformed JSON request or invalid field value", "VAL_002"),
                HttpStatus.BAD_REQUEST
        );
    }

    // ================= FILE STORAGE & UPLOAD =================

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {

        log.warn("Spring Multipart size limit exceeded: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error("File size exceeds maximum limit of 10MB", "FILE_001"),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    @ExceptionHandler(MaximumFileSizeException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaximumFileSizeException(
            MaximumFileSizeException ex) {

        log.warn("File storage limit exceeded: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "FILE_001"),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    @ExceptionHandler(FailedToStoreFileException.class)
    public ResponseEntity<ApiResponse<Void>> handleFailedToStoreFileException(
            FailedToStoreFileException ex) {

        log.error("File storage operation failed: ", ex);

        return new ResponseEntity<>(
                ApiResponse.error("Failed to store profile image. Please try again.", "FILE_002"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.warn("Invalid file or input argument: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "FILE_003"),
                HttpStatus.BAD_REQUEST
        );
    }

    // ================= AUTH / USER =================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_002"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFoundException(MemberNotFoundException ex) {
        log.warn("Member not found: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "MEM_001"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.warn("Invalid credentials attempt: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_001"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoginFailedException(LoginFailedException ex) {
        log.warn("Login failure: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_005"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenExpiredException(TokenExpiredException ex) {
        log.warn("JWT Token expired: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_003"),
                HttpStatus.UNAUTHORIZED
        );
    }

    // ================= REGISTRATION =================

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistsException(EmailAlreadyExistException ex) {
        log.warn("Registration conflict - email exists: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "REG_001"),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleRegistrationFailedException(RegistrationFailedException ex) {
        log.error("Registration process failed: ", ex);
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "REG_002"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ================= APPROVAL =================

    @ExceptionHandler(ApprovalRequiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleApprovalRequiredException(ApprovalRequiredException ex) {
        log.warn("Approval required block: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_004"),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ApprovalRejectionException.class)
    public ResponseEntity<ApiResponse<Void>> handleApprovalRejectionException(ApprovalRejectionException ex) {
        log.warn("User approval rejected: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "AUTH_006"),
                HttpStatus.FORBIDDEN
        );
    }

    // ================= EMAIL =================

    @ExceptionHandler(FailedToSendPasswordResetEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailFailure(FailedToSendPasswordResetEmailException ex) {
        log.error("Email delivery failed: ", ex);
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "EMAIL_001"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ================= GENERAL =================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), "RES_001"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        // Ignore Swagger/OpenAPI endpoints so the UI renders correctly
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            throw new RuntimeException(ex);
        }

        log.error("Unhandled exception occurred at path: {}", path, ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "An unexpected error occurred",
                        "SYS_001"
                ));
    }
}