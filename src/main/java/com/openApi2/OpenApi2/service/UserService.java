package com.openApi2.OpenApi2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.repository.CoachRepository;
import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import com.openApi2.OpenApi2.service.entity.User;
import com.openApi2.OpenApi2.validation.UserNotFoundException;
import com.openApi2.OpenApi2.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate ;
    @Autowired
    private UserValidator userValidator;

    final String baseUrl8080 = "http://localhost:8080/users/";
    //GET
    public User getUserByIdFrom8080(String idUser) {
        String url = baseUrl8080 + idUser;

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, null, User.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new UserNotFoundException();
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException();
        }
    }

    public User getUserById(String idUser) {
        // Intenta buscar el usuario en la API en el puerto 8080
        User user = getUserByIdFrom8080(idUser);

        // Si el usuario no se encuentra en la API en el puerto 8080, intenta buscarlo en 8081
        if (user == null) {
            user = coachRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
        }

        // Si el usuario se encuentra en la API en el puerto 8080 y no existe en el 8081, guárdalo en el 8081
        if (user != null && coachRepository.findById(idUser).isEmpty()) {
            coachRepository.save(user);
        }

        return user;
    }
    //PATCH
    public User applyPatchToUser8081(JsonPatch patch, User user) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(user, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }

    public void updateUserPatch8081(User updatedUser) {
        coachRepository.save(updatedUser);
    }
    public User applyPatchTo8081(String idUser, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        User user = coachRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
        User userPatched = applyPatchToUser8081(patch, user);
        updateUserPatch8081(userPatched);
        return userPatched;
    }
    //PUT
    public User updateUser(String idUser, BodyUserPut bodyUserPut) {
        // Buscar al usuario actual por su ID en MongoDB
        User user = coachRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

        if (userValidator.putIsValid(bodyUserPut)) {
            user.setFirst_name(bodyUserPut.getFirst_name());
            user.setSecond_name(bodyUserPut.getSecond_name());
            user.setFirst_surname(bodyUserPut.getFirst_surname());
            user.setEmail(bodyUserPut.getEmail());

            return coachRepository.save(user);
        }

        return null;
    }

}
