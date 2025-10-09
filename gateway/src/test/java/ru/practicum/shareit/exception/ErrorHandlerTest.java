package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Test error");

        Map<String, String> result = errorHandler.handleIllegalArgumentException(exception);

        assertNotNull(result);
        assertEquals("Test error", result.get("error"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        Map<String, String> result = errorHandler.handleConstraintViolationException(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.get("error"));
    }

    @Test
    void handleMissingRequestHeaderException_ShouldReturnBadRequest() {
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);

        Map<String, String> result = errorHandler.handleMissingRequestHeaderException(exception);

        assertNotNull(result);
        assertEquals("Required header is missing", result.get("error"));
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

        Map<String, String> result = errorHandler.handleMethodArgumentTypeMismatchException(exception);

        assertNotNull(result);
        assertEquals("Invalid parameter type", result.get("error"));
    }

    @Test
    void handleOtherExceptions_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Test error");

        Map<String, String> result = errorHandler.handleOtherExceptions(exception);

        assertNotNull(result);
        assertEquals("Internal server error", result.get("error"));
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("approved", "Boolean");

        Map<String, String> result = errorHandler.handleMissingServletRequestParameterException(exception);

        assertNotNull(result);
        assertEquals("Required parameter is missing", result.get("error"));
    }
}