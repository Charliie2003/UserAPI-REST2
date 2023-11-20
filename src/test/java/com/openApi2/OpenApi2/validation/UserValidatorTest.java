package com.openApi2.OpenApi2.validation;

import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @Spy
    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private BodyUserPut bodyUserPut;
    @BeforeEach
    void setUp() {

    }
    @Test
    void testContainsInvalidString_ShouldReturnTrueForInvalidString() {
        // Arrange
        String invalidString = "string";

        // Act
        boolean result = userValidator.containsInvalidString(invalidString);

        // Assert
        assertTrue(result);
    }

    @Test
    void testContainsInvalidString_ShouldReturnFalseForValidString() {
        // Arrange
        String validString = "valid";

        // Act
        boolean result = userValidator.containsInvalidString(validString);

        // Assert
        assertFalse(result);
    }

    @Test
    void testContainsInvalidString_ShouldReturnFalseForNull() {
        // Act
        boolean result = userValidator.containsInvalidString(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testContainsInvalidString_ShouldReturnFalseForEmptyString() {
        // Arrange
        String emptyString = "";

        // Act
        boolean result = userValidator.containsInvalidString(emptyString);

        // Assert
        assertFalse(result);
    }

    @Test
    void testContainsInvalidString_ShouldReturnFalseForWhitespaceString() {
        // Arrange
        String whitespaceString = "   ";

        // Act
        boolean result = userValidator.containsInvalidString(whitespaceString);

        // Assert
        assertFalse(result);
    }
    @Test
    public void testPutIsValidWithNullBodyShouldReturnFalse() {
        boolean result = userValidator.putIsValid(null);
        assertFalse(result);
    }
    @Test
    public void testPutIsValidWithInvalidFirstNameShouldReturnFalse() {
        when(bodyUserPut.getFirst_name()).thenReturn("invalidFirstName");
        when(userValidator.containsInvalidString(anyString())).thenReturn(true);

        boolean result = userValidator.putIsValid(bodyUserPut);
        assertFalse(result);
    }
    @Test
    public void testPutIsValidWithValidBodyShouldReturnTrue() {
        when(bodyUserPut.getFirst_name()).thenReturn("validFirstName");
        when(bodyUserPut.getSecond_name()).thenReturn("validSecondName");
        when(bodyUserPut.getFirst_surname()).thenReturn("validFirstSurname");
        when(bodyUserPut.getEmail()).thenReturn("validEmail@example.com");
        when(userValidator.containsInvalidString(anyString())).thenReturn(false);

        boolean result = userValidator.putIsValid(bodyUserPut);
        assertTrue(result);
    }


}