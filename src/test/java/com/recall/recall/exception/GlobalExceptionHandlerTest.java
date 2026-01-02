package com.recall.recall.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GlobalExceptionHandlerTest {

    @MockitoBean
    private BindingResult bindingResult;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Handle not valid method arguments")
    void testHandleMethodArgumentNotValidException() {
        FieldError fieldError1 = new FieldError("customer", "name", "Name is required");
        FieldError fieldError2 = new FieldError("customer", "email", "Email is invalid");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<?> response = globalExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Validation Error", body.get("error"));
        assertNotNull(body.get("timestamp"));

        List<String> messages = (List<String>) body.get("message");
        assertEquals(2, messages.size());
        assertTrue(messages.contains("name: Name is required"));
        assertTrue(messages.contains("email: Email is invalid"));
    }

    @Test
    @DisplayName("Handle constraint violation exception")
    void testHandleConstraintViolationException() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("email");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Enter a valid email");

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("name");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Name cannot be blank");

        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        ResponseEntity<?> response = globalExceptionHandler.handleConstraintViolationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Constraint Violation", body.get("error"));
        assertNotNull(body.get("timestamp"));

        List<String> messages = (List<String>) body.get("message");
        assertEquals(2, messages.size());
    }

    @Test
    @DisplayName("Handle entity not found exception")
    void testHandleEntityNotFoundException() {
        String errorMessage = "Customer not found with id: 1";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        ResponseEntity<?> response = globalExceptionHandler.handleEntityNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));
        assertEquals("Resource Not Found", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertInstanceOf(LocalDateTime.class, body.get("timestamp"));
    }

    @Test
    @DisplayName("Handle HTTP message not readable exception")
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<?> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Invalid Request", body.get("error"));
        assertEquals("Malformed JSON request body or invalid field types", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Handle illegal argument exception")
    void testHandleIllegalArgumentException() {
        String errorMessage = "Invalid page number";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<?> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Invalid Argument", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Handle generic exception")
    void testHandleGenericException() {
        Exception exception = new Exception("Unexpected error occurred");

        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Unexpected error occurred", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Handle method argument not valid exception with empty errors")
    void testHandleMethodArgumentNotValidExceptionWithEmptyErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<?> response = globalExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        List<String> messages = (List<String>) body.get("message");
        assertTrue(messages.isEmpty());
    }

    @Test
    @DisplayName("Handle constraint violation exception with empty violations")
    void testHandleConstraintViolationExceptionWithEmptyViolations() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        ResponseEntity<?> response = globalExceptionHandler.handleConstraintViolationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        List<String> messages = (List<String>) body.get("message");
        assertTrue(messages.isEmpty());
    }

    @Test
    @DisplayName("Verify response body structure")
    void testResponseBodyStructure() {
        Exception exception = new Exception("Test error");

        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(exception);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    @DisplayName("Verify timestamp is recent")
    void testTimestampIsRecent() {
        Exception exception = new Exception("Test error");

        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(exception);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertNotNull(timestamp);

        LocalDateTime now = LocalDateTime.now();
        assertTrue(timestamp.isBefore(now.plusSeconds(1)));
        assertTrue(timestamp.isAfter(now.minusSeconds(5)));
    }
}

