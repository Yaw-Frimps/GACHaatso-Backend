package com.example.gacapp.exception;

public class MaximumFileSizeException extends RuntimeException {
    public MaximumFileSizeException(String message) {
        super(message);
    }
}
