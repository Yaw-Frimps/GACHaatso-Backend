package com.example.gacapp.exception;

public class ApprovalRequiredException extends RuntimeException {
    public ApprovalRequiredException(String message) {
        super(message);
    }
}
