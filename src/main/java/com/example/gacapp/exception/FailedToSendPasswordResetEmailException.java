package com.example.gacapp.exception;

public class FailedToSendPasswordResetEmailException extends RuntimeException {

    public FailedToSendPasswordResetEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    public FailedToSendPasswordResetEmailException(String message) {
        super(message);
    }
}
