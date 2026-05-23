package com.example.gacapp.exception;

public class MaxUploadSizeExceededException extends RuntimeException {
    public MaxUploadSizeExceededException(String message) {
        super(message);
    }
}
