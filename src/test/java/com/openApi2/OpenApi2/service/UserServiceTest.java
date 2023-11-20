package com.openApi2.OpenApi2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.openApi2.OpenApi2.repository.CoachRepository;
import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import com.openApi2.OpenApi2.service.entity.User;
import com.openApi2.OpenApi2.validation.UserNotFoundException;
import com.openApi2.OpenApi2.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CoachRepository coachRepository;
    @Spy
    @InjectMocks
    private UserService userService;
    @Mock
    private RestTemplate restTemplate ;
    @Mock
    private UserValidator userValidator;
    private User user;

    private BodyUserPut bodyUserPut;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        bodyUserPut = new BodyUserPut();
        user = new User();
        user.setId("123");
        user.setFirst_name("John");
        user.setSecond_name("Doe");
        user.setFirst_surname("Smith");
        user.setEmail("john.doe@example.com");
    }
    //GET
    @Test
    void getUserByIdFrom8080_ShouldReturnUser_WhenUserExists() {
        // Configuración
        String idUser = "123";
        String url = "http://localhost:8080/users/" + idUser;

        ResponseEntity<User> myEntity = new ResponseEntity<>(user, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(User.class))
        ).thenReturn(myEntity);

        // Ejecución
        User actualUser = userService.getUserByIdFrom8080(idUser);

        // Verificación
        assertEquals(user, actualUser);
        assertNotNull(actualUser);
    }
    @Test
    void getUserByIdFrom8080_ShouldReturnNull_WhenUserNotFound() {
        // Configuración
        String idUser = "456";
        String url = "http://localhost:8080/users/" + idUser;

        ResponseEntity<User> myEntity = new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(User.class))
        ).thenReturn(myEntity);


        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByIdFrom8080(idUser);
        });
    }
    @Test
    void getUserById_ShouldReturnUserFrom8080_WhenFoundIn8080() {
        // Configuración
        String idUser = "123";
        String url = "http://localhost:8080/users/" + idUser;

        ResponseEntity<User> myEntity = new ResponseEntity<>(user, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(User.class))
        ).thenReturn(myEntity);


        // Simula que coachRepository.findById devuelve un Optional con el usuario (usuario encontrado en 8081)
        when(coachRepository.findById(idUser)).thenReturn(Optional.of(user));

        // Ejecución
        User actualUser = userService.getUserById(idUser);

        // Verificación
        assertEquals(user, actualUser);
        verify(userService, times(1)).getUserByIdFrom8080(idUser);
        verify(coachRepository, times(1)).findById(idUser);
    }
    @Test
    public void getUserById_UserIsNull_ReturnsNull() {
        String idUser = "456";
        String url = "http://localhost:8080/users/" + idUser;

        // Simula que RestTemplate devuelve una respuesta con HttpStatus.OK
        ResponseEntity<User> myEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(User.class))
        ).thenReturn(myEntity);

        // Simula que coachRepository.findById retorna un Optional vacío
        when(coachRepository.findById(idUser)).thenReturn(Optional.empty());

        User result = userService.getUserById(idUser);

        // Verifica que el resultado es null
        assertNull(result);

        // Verifica que se llamó a coachRepository.findById
        verify(coachRepository).findById(idUser);

        // Verifica que no se llamó a coachRepository.save
        verify(coachRepository, never()).save(any(User.class));
    }
    @Test
    public void getUserById_UserFoundIn8080AndNotInRepository_SavesUser() {
        String idUser = "123";
        String url = "http://localhost:8080/users/" + idUser;


        // Simula que RestTemplate devuelve una respuesta con HttpStatus.OK y un usuario
        ResponseEntity<User> myEntity = new ResponseEntity<>(user, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(User.class))
        ).thenReturn(myEntity);

        // Simula que coachRepository.findById retorna un Optional vacío
        when(coachRepository.findById(idUser)).thenReturn(Optional.empty());

        User result = userService.getUserById(idUser);

        // Verifica que se llamó a coachRepository.save con el usuario correcto
        verify(coachRepository).save(result);
    }
    @Test
    void applyPatchToUser_ShouldUpdateUser() throws JsonPatchException, IOException {
        //Crear un JsonPatch para la prueba
        String patchString = "[{\"op\": \"replace\", \"path\": \"/first_name\", \"value\": \"Jane\"}]";
        JsonPatch patch = JsonPatch.fromJson(new ObjectMapper().readTree(patchString));

        //Mockear el comportamiento del objectMapper para convertir User a JsonNode
        JsonNode userNode = new ObjectMapper().convertValue(user, JsonNode.class);
        when(objectMapper.convertValue(any(User.class), eq(JsonNode.class))).thenReturn(userNode);

        //Mockear el objectMapper para convertir un JsonNode de vuelta a un User
        when(objectMapper.treeToValue(any(JsonNode.class), eq(User.class))).thenAnswer(invocation -> {
            JsonNode node = (JsonNode) invocation.getArguments()[0];
            return new ObjectMapper().treeToValue(node, User.class); // Convertir el nodo modificado de vuelta a User
        });

        //Llamar al método bajo prueba
        User result = userService.applyPatchToUser8081(patch, user);

        // Verificar el resultado
        assertEquals("Jane", result.getFirst_name());
    }
    @Test
    public void testUpdateUserPatch() {
        user.setFirst_name("Charlie");
        user.setSecond_name("Hinojosa");

        when(coachRepository.save(user)).thenReturn(user);

        userService.updateUserPatch8081(user);

        verify(coachRepository).save(user);

    }
    @Test
    void whenApplyPatchToUser8080Called_ThenPatchAndSaveUser() throws JsonPatchException, IOException {
        // Given
        String idUser = "123";
        String patchString = "[{\"op\": \"replace\", \"path\": \"/first_name\", \"value\": \"Jane\"}]";
        JsonPatch patch = JsonPatch.fromJson(new ObjectMapper().readTree(patchString));


        when(coachRepository.findById(idUser)).thenReturn(Optional.of(user));
        when(objectMapper.convertValue(any(User.class), eq(JsonNode.class))).thenReturn(new ObjectMapper().convertValue(user, JsonNode.class));
        when(objectMapper.treeToValue(any(JsonNode.class), eq(User.class))).thenAnswer(invocation -> {
            JsonNode node = (JsonNode) invocation.getArguments()[0];
            return new ObjectMapper().treeToValue(node, User.class); // Convertir el nodo modificado de vuelta a User
        });

        // When
        User patchedUser = userService.applyPatchTo8081(idUser,patch);

        // Then
        assertEquals("Jane", patchedUser.getFirst_name()); // Asegurar que el nombre se ha actualizado
        verify(coachRepository).save(patchedUser); // Verificar que el usuario modificado se guarda en el repositorio
    }
    @Test
    public void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        String idUser = "123";
        when(coachRepository.findById(idUser)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(idUser, bodyUserPut));
    }
    @Test
    public void updateUser_ValidationFails_ReturnsNull() {
        String idUser = "123";
        User mockUser = new User();
        when(coachRepository.findById(idUser)).thenReturn(Optional.of(mockUser));
        when(userValidator.putIsValid(any(BodyUserPut.class))).thenReturn(false);

        User result = userService.updateUser(idUser, bodyUserPut);
        assertNull(result);
    }
    @Test
    void testUpdateUserValid() {
        String userId = user.getId();
        String newName = "Charlie";

        bodyUserPut.setFirst_name(newName);

        when(coachRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userValidator.putIsValid(bodyUserPut)).thenReturn(true);

        User updateUser = userService.updateUser(userId, bodyUserPut);

        assertEquals(newName, user.getFirst_name());

    }




}