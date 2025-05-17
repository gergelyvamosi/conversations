package org.nextweb.conversations;

import org.nextweb.converstions.controller.UserController;
import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(User.builder().id(1L).name("User 1").build());
        users.add(User.builder().id(2L).name("User 2").build());
        when(userRepository.findAll()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Found() {
        // Arrange
        User user = User.builder().id(1L).name("User 1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Optional<User>> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(user), response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Optional<User>> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Optional.empty(), response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser() {
        // Arrange
        User userToSave = User.builder().name("New User").build();
        User savedUser = User.builder().id(1L).name("New User").build();
        when(userRepository.save(userToSave)).thenReturn(savedUser);

        // Act
        ResponseEntity<User> response = userController.createUser(userToSave);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void testUpdateUser_Found() {
        // Arrange
        Long id = 1L;
        User existingUser = User.builder().id(id).name("Old Name").build();
        User updatedUserDetails = User.builder().name("New Name").build();
        User updatedUser = User.builder().id(id).name("New Name").build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // Act
        ResponseEntity<User> response = userController.updateUser(id, updatedUserDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_NotFound() {
        // Arrange
        Long id = 1L;
        User updatedUserDetails = User.builder().name("New Name").build();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.updateUser(id, updatedUserDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<Void> response = userController.deleteUser(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository, times(1)).deleteById(id);
    }
}