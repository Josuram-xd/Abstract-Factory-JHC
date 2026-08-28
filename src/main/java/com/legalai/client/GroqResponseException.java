package com.legalai.client;

public class GroqResponseException extends Exception {

    public GroqResponseException(String message) {
        super(message);
    }

    public GroqResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
