package com.openApi2.OpenApi2.controllers;

import com.couchbase.client.core.error.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.service.RemoteUserService;
import com.openApi2.OpenApi2.service.UserService;
import com.openApi2.OpenApi2.service.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/users")
@Tag(name = "users", description = "Maneja la información de los usuarios")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RemoteUserService remoteUserService;
    @GetMapping("/{idUser}")
    @Operation(summary = "Obtiene la información de usuario por ID")
    public ResponseEntity<User> getUser(@Valid @PathVariable String idUser) {
        User user = userService.getUserById(idUser);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    @PatchMapping(path = "/{idUser}", consumes = "application/json-patch+json")
    public ResponseEntity<User> partialUpdateUser(@PathVariable String idUser, @RequestBody JsonPatch patch) {
        try {
            User patchedUser = userService.applyPatchToUser(idUser, patch);
            userService.updateUserPatch(patchedUser);
            return ResponseEntity.ok(patchedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
