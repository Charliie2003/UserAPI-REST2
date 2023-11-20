package com.openApi2.OpenApi2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import com.openApi2.OpenApi2.service.UserService;
import com.openApi2.OpenApi2.service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;


    @InjectMocks
    private UserController userController;
    private User user;
    private BodyUserPut bodyUserPut;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Crear un usuario para utilizar en la prueba
        user = new User();
        bodyUserPut = new BodyUserPut();
        user.setId("123");
        user.setFirst_name("John");
        user.setSecond_name("Doe");
        user.setFirst_surname("Smith");
        user.setEmail("john.doe@example.com");

    }
    //GET
    @Test
    public void whenGetUserWithValidId_thenUserIsReturned() {
        String idUser = user.getId();


        when(userService.getUserById(idUser)).thenReturn(user);

        ResponseEntity<User> response = userController.getUser(idUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void whenGetUserWithInvalidId_thenNotFound() {
        String idUser = "423";

        when(userService.getUserById(idUser)).thenReturn(null);

        ResponseEntity<User> response = userController.getUser(idUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //PUT
    @Test
    public void whenUpdateExistingUser_thenUserIsReturned() {
        String idUser = user.getId();

        User updatedUser = new User(); // Configura con datos de usuario actualizados

        when(userService.updateUser(eq(idUser), any(BodyUserPut.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(idUser, bodyUserPut);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }
    @Test
    public void whenUpdateNonExistingUser_thenNotFound() {
        String idUser = "456";

        when(userService.updateUser(eq(idUser), any(BodyUserPut.class))).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(idUser, bodyUserPut);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void whenPatchExistingUser_thenUserIsPatched() throws JsonPatchException, IOException {
        String idUser = user.getId();
        String patchString = "[{\"op\": \"replace\", \"path\": \"/first_name\", \"value\": \"Jane\"}]";
        JsonPatch patch = JsonPatch.fromJson(new ObjectMapper().readTree(patchString));


        when(userService.applyPatchTo8081(eq(idUser), any(JsonPatch.class))).thenReturn(user);


        ResponseEntity<User> response = userController.partialUpdateUser(idUser, patch);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
}