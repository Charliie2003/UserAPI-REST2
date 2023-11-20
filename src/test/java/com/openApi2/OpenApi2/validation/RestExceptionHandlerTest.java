package com.openApi2.OpenApi2.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {
    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @Test
    void handleUserNotFoundException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException();

        // Act
        ResponseEntity<String> response = restExceptionHandler.handleUserNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void handleGenericException() {
        // Arrange
        Exception exception = new Exception("Error interno del servidor");

        // Act
        ResponseEntity<String> response = restExceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody());
    }
}