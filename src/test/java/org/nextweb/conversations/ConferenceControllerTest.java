package org.nextweb.conversations;

import org.nextweb.converstions.controller.ConferenceController;
import org.nextweb.converstions.model.Conference;
import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.ConferenceRepository;
import org.nextweb.converstions.repository.UserRepository;
import org.nextweb.converstions.util.UserHelper;
import org.nextweb.converstions.util.SystemLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConferenceControllerTest {

    @Mock
    private ConferenceRepository conferenceRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ConferenceController conferenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllConferences() {

        // Mock SecurityContextHolder
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(UserHelper.ROOT_USER);  // Set the username you want to return
        MockedStatic<UserHelper> mockedStatic = Mockito.mockStatic(UserHelper.class);
        mockedStatic.when(UserHelper::getAuthenticatedUsername).thenReturn(UserHelper.ROOT_USER);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Arrange
        List<Conference> conferences = new ArrayList<>();
        conferences.add(Conference.builder().id(1L).title("Conference 1").build());
        conferences.add(Conference.builder().id(2L).title("Conference 2").build());
        when(conferenceRepository.findAll()).thenReturn(conferences);

        // Act
        ResponseEntity<List<Conference>> response = conferenceController.getAllConferences();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conferences, response.getBody());
        verify(conferenceRepository, times(1)).findAll();
    }

    @Test
    void testGetAllConferencesNonRoot() {

        // Arrange
        User user = User.builder().id(1L).name(UserHelper.ANONYMOUS_USER).build();
        when(userRepository.findByName(user.getName())).thenReturn(Optional.of(user));
        List<User> users = new ArrayList<>();
        users.add(User.builder().id(1L).name(UserHelper.ANONYMOUS_USER).build());
        when(userRepository.findAll()).thenReturn(users);

        List<Conference> conferences = new ArrayList<>();
        conferences.add(Conference.builder().id(1L).title("Conference 1").users(users).build());
        conferences.add(Conference.builder().id(2L).title("Conference 2").users(users).build());
        when(conferenceRepository.findByUsersId(user.getId())).thenReturn(conferences);

        // Act
        ResponseEntity<List<Conference>> response = conferenceController.getAllConferences();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conferences, response.getBody());
        assertEquals(conferences.size(), (response.getBody() != null ? response.getBody().size() : null));
        verify(conferenceRepository, times(1)).findByUsersId(user.getId());
    }

    @Test
    void testGetConferenceById_Found() {
        // Arrange
        Conference conference = Conference.builder().id(1L).title("Conference 1").build();
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));

        // Act
        ResponseEntity<Optional<Conference>> response = conferenceController.getConferenceById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(conference), response.getBody());
        verify(conferenceRepository, times(1)).findById(1L);
    }

    @Test
    void testGetConferenceById_NotFound() {
        // Arrange
        when(conferenceRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Optional<Conference>> response = conferenceController.getConferenceById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Optional.empty(), response.getBody());
        verify(conferenceRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateConference() {
        // Arrange
        Conference conferenceToSave = Conference.builder().title("New Conference").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();
        Conference savedConference = Conference.builder().id(1L).title("New Conference").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();
        when(conferenceRepository.save(conferenceToSave)).thenReturn(savedConference);

        // Act
        ResponseEntity<Conference> response = conferenceController.createConference(conferenceToSave);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedConference, response.getBody());
        verify(conferenceRepository, times(1)).save(conferenceToSave);
    }

    @Test
    void testUpdateConference_Found() {
        // Arrange
        Long id = 1L;
        Conference existingConference = Conference.builder().id(id).title("Old Title").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();
        Conference updatedConferenceDetails = Conference.builder().title("New Title").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();
        Conference updatedConference = Conference.builder().id(id).title("New Title").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();

        when(conferenceRepository.findById(id)).thenReturn(Optional.of(existingConference));
        when(conferenceRepository.save(existingConference)).thenReturn(updatedConference);

        // Act
        ResponseEntity<Conference> response = conferenceController.updateConference(id, updatedConferenceDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedConference, response.getBody());
        verify(conferenceRepository, times(1)).findById(id);
        verify(conferenceRepository, times(1)).save(existingConference);
    }

    @Test
    void testUpdateConference_NotFound() {
        // Arrange
        Long id = 1L;
        Conference updatedConferenceDetails = Conference.builder().title("New Title").plannedDate(LocalDate.now()).description("Desc").location("Loc").build();
        when(conferenceRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Conference> response = conferenceController.updateConference(id, updatedConferenceDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conferenceRepository, times(1)).findById(id);
        verify(conferenceRepository, never()).save(any(Conference.class));
    }

    @Test
    void testDeleteConference() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<Void> response = conferenceController.deleteConference(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(conferenceRepository, times(1)).deleteById(id);
    }

    @Test
    void testAddUserToConference_Found() {
        // Arrange
        Long conferenceId = 1L;
        Long userId = 1L;
        Conference conference = Conference.builder().id(conferenceId).title("Conference 1").users(new ArrayList<>()).build();
        User user = User.builder().id(userId).name("User 1").build();
        when(conferenceRepository.findById(conferenceId)).thenReturn(Optional.of(conference));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conferenceRepository.save(conference)).thenReturn(conference);

        // Act
        ResponseEntity<Void> response = conferenceController.addUserToConference(conferenceId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, conference.getUsers().size());
        assertEquals(user, conference.getUsers().get(0));
        verify(conferenceRepository, times(1)).findById(conferenceId);
        verify(userRepository, times(1)).findById(userId);
        verify(conferenceRepository, times(1)).save(conference);
    }

    @Test
    void testAddUserToConference_NotFound() {
        // Arrange
        Long conferenceId = 1L;
        Long userId = 1L;
        when(conferenceRepository.findById(conferenceId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).name("User1").build()));

        // Act
        ResponseEntity<Void> response = conferenceController.addUserToConference(conferenceId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conferenceRepository, times(1)).findById(conferenceId);
        verify(userRepository, times(1)).findById(userId);
        verify(conferenceRepository, never()).save(any(Conference.class));
    }

     @Test
    void testRemoveUserFromConference_Found() {
        // Arrange
        Long conferenceId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("User 1").build();
        List<User> users = new ArrayList<>();
        users.add(user);
        Conference conference = Conference.builder().id(conferenceId).title("Conference 1").users(users).build();

        when(conferenceRepository.findById(conferenceId)).thenReturn(Optional.of(conference));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conferenceRepository.save(conference)).thenReturn(conference);

        // Act
        ResponseEntity<Void> response = conferenceController.removeUserFromConference(conferenceId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, conference.getUsers().size());
        verify(conferenceRepository, times(1)).findById(conferenceId);
        verify(userRepository, times(1)).findById(userId);
        verify(conferenceRepository, times(1)).save(conference);
    }

    @Test
    void testRemoveUserFromConference_NotFound() {
        // Arrange
        Long conferenceId = 1L;
        Long userId = 1L;
        when(conferenceRepository.findById(conferenceId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).name("User1").build()));

        // Act
        ResponseEntity<Void> response = conferenceController.removeUserFromConference(conferenceId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conferenceRepository, times(1)).findById(conferenceId);
        verify(userRepository, times(1)).findById(userId);
        verify(conferenceRepository, never()).save(any(Conference.class));
    }
}