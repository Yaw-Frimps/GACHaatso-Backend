package com.example.gacapp.exception;


public class DataInitialisationException extends RuntimeException {

    public DataInitialisationException(String message) {
        super(message);
    }

    public DataInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }
}
