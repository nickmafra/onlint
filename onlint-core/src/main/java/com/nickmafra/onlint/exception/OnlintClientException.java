package com.nickmafra.onlint.exception;

public class OnlintClientException extends RuntimeException {
    public OnlintClientException() {
    }

    public OnlintClientException(String message) {
        super(message);
    }

    public OnlintClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlintClientException(Throwable cause) {
        super(cause);
    }
}
