package com.example.gacapp.exception;


public class FailedToStoreFileException extends RuntimeException {
    public FailedToStoreFileException(String message) {
        super(message);
    }
    public FailedToStoreFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
