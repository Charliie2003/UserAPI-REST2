package com.openApi2.OpenApi2.service;

import com.couchbase.client.core.error.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.repository.CoachRepository;
import com.openApi2.OpenApi2.service.entity.User;
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
    private RemoteUserService remoteUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    //GET
    public User getUserById(String idUser) {
        // Intenta buscar el usuario en la API en el puerto 8080
        User user = remoteUserService.getUserByIdFrom8080(idUser);

        // Si el usuario no se encuentra en la API en el puerto 8080, intenta buscarlo en 8081
        if (user == null) {
            user = coachRepository.findById(idUser).orElse(null);
        }

        return user;
    }
    public User applyPatchToUser(String idUser, JsonPatch patch) throws JsonPatchException, JsonProcessingException, UserNotFoundException {
        User user = null;

        // Intenta aplicar el parche al usuario en el puerto 8080
        try {
            user = restTemplate.getForObject("http://localhost:8080/users/" + idUser, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            // El usuario no se encontró en el puerto 8080
        }

        if (user != null) {
            JsonNode patched = patch.apply(objectMapper.convertValue(user, JsonNode.class));

            return objectMapper.treeToValue(patched, User.class);
        }

        // Si el usuario no se encuentra en el puerto 8080 aplica el parche al usuario en el puerto 8081
        try {
            user = restTemplate.getForObject("http://localhost:8081/users/" + idUser, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            // El usuario no se encontró en el puerto 8081
        }

        if (user != null) {
            JsonNode patched = patch.apply(objectMapper.convertValue(user, JsonNode.class));

            return objectMapper.treeToValue(patched, User.class);

        }

        return user; // El usuario no se encouentra
    }
    public void updateUserPatch(User updatedUser) {
        coachRepository.save(updatedUser);
    }
    private User patchUserByIdFrom8080(String url, JsonPatch patch) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crea una entidad Http con el JSON Patch
        HttpEntity<JsonPatch> requestEntity = new HttpEntity<>(patch, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);
            // Devuelve el usuario actualizado
            return restTemplate.getForObject(url, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            // El usuario no se encontró en el puerto especificado
            return null;
        }
    }


}
