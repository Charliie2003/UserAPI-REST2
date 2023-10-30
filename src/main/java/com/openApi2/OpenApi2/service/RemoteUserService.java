package com.openApi2.OpenApi2.service;

import com.couchbase.client.core.error.UserNotFoundException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteUserService {
    @Autowired
    private RestTemplate restTemplate;

    public User getUserByIdFrom8080(String idUser) {
        String url = "http://localhost:8080/users/" + idUser;
        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, null, User.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (HttpClientErrorException.NotFound e) {
            // El usuario no se encontró en la API en el puerto 8080
        }
        return null;
    }

}
