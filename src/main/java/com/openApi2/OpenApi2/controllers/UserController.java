package com.openApi2.OpenApi2.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.repository.CoachRepository;
import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import com.openApi2.OpenApi2.service.UserService;
import com.openApi2.OpenApi2.service.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "users", description = "Maneja la información de los usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{idUser}")
    @Operation(summary = "Obtiene la información de usuario por ID")
    public ResponseEntity<User> getUser(@Valid @PathVariable String idUser) {
        User user = userService.getUserById(idUser);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{idUser}")
    @Operation(summary = "Actualiza la información de un usuario por ID")
    public ResponseEntity<User> updateUser(@Valid @PathVariable String idUser,@Valid @RequestBody BodyUserPut bodyUserPut) {
        User user = userService.updateUser(idUser, bodyUserPut);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PatchMapping(path= "/{idUser}", consumes = "application/json-patch+json")
    @Operation(summary = "Modifica parcialmente la información de un usuario por ID")
    public ResponseEntity<User> partialUpdateUser(@Valid @PathVariable String idUser, @Valid @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
            User userPatched = userService.applyPatchTo8081(idUser, patch);
            return ResponseEntity.ok(userPatched);
    }

}
