package com.itau.pixms.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldHandlePixMsException() {
        FraudMarkerException exception = new FraudMarkerException("Key has fraud marker");

        ProblemDetail result = globalExceptionHandler.handleJBankException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatus());
        assertEquals("Key Fraud Marker error", result.getTitle());
        assertEquals("Key has fraud marker", result.getDetail());
    }

    @Test
    void shouldHandleGenerateTokenException() {
        GenerateTokenException exception = new GenerateTokenException("Failed to generate token");

        ProblemDetail result = globalExceptionHandler.handleJBankException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Generate Token Exception", result.getTitle());
        assertEquals("Failed to generate token", result.getDetail());
    }

    @Test
    void shouldHandleInvalidDebtorException() {
        InvalidDebtorException exception = new InvalidDebtorException("Invalid debtor information");

        ProblemDetail result = globalExceptionHandler.handleJBankException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatus());
        assertEquals("Invalid Debtor", result.getTitle());
        assertEquals("Invalid debtor information", result.getDetail());
    }

    @Test
    void shouldHandleInvalidPixKeyException() {
        InvalidPixKeyException exception = new InvalidPixKeyException("Invalid PIX key format");

        ProblemDetail result = globalExceptionHandler.handleJBankException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatus());
        assertEquals("Invalid Pix Key", result.getTitle());
        assertEquals("Invalid PIX key format", result.getDetail());
    }
}