package com.itau.pixsub.exception;

public class PixChargeProcessingException extends RuntimeException {
    public PixChargeProcessingException(String message) {
        super(message);
    }

    public PixChargeProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
