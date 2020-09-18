package com.nickmafra.onlint.exception;

public class OnlintRuntimeException extends RuntimeException {
    public OnlintRuntimeException() {
    }

    public OnlintRuntimeException(String message) {
        super(message);
    }

    public OnlintRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlintRuntimeException(Throwable cause) {
        super(cause);
    }
}
