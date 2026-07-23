package com.example.gacapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;

    // ================= SUCCESS RESPONSES =================

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }

    // ================= ERROR RESPONSES =================

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(errors)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, T errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .data(errors)
                .timestamp(LocalDateTime.now(DEFAULT_ZONE))
                .build();
    }
}