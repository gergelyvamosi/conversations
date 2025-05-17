package org.nextweb.conversations;

import org.nextweb.converstions.controller.ConversationController;
import org.nextweb.converstions.model.Conversation;
import org.nextweb.converstions.repository.ConversationRepository;
import org.nextweb.converstions.util.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ConversationControllerTest {

    @Mock
    private ConversationRepository conversationRepository;
    @InjectMocks
    private ConversationController conversationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllConversations() {
        // Arrange
        List<Conversation> conversations = new ArrayList<>();
        conversations.add(Conversation.builder().id(1L).text("Conversation 1").build());
        conversations.add(Conversation.builder().id(2L).text("Conversation 2").build());
        when(conversationRepository.findAll()).thenReturn(conversations);

        // Act
        ResponseEntity<List<Conversation>> response = conversationController.getAllConversations();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conversations, response.getBody());
        verify(conversationRepository, times(1)).findAll();
    }

    @Test
    void testGetConversationById_Found() {
        // Arrange
        Conversation conversation = Conversation.builder().id(1L).text("Conversation 1").build();
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        // Act
        ResponseEntity<Optional<Conversation>> response = conversationController.getConversationById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(conversation), response.getBody());
        verify(conversationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetConversationById_NotFound() {
        // Arrange
        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Optional<Conversation>> response = conversationController.getConversationById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Optional.empty(), response.getBody());
        verify(conversationRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateConversation() {
        // Arrange
        Conversation conversationToSave = Conversation.builder().text("New Conversation").createdTimestamp(LocalDateTime.now()).archived("N").build();
        Conversation savedConversation = Conversation.builder().id(1L).text("New Conversation").createdTimestamp(LocalDateTime.now()).archived("N").build();
        when(conversationRepository.save(conversationToSave)).thenReturn(savedConversation);

        // Act
        ResponseEntity<Conversation> response = conversationController.createConversation(conversationToSave);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedConversation, response.getBody());
        verify(conversationRepository, times(1)).save(conversationToSave);
    }

    @Test
    void testUpdateConversation_Found() {
        // Arrange
        Long id = 1L;
        Conversation existingConversation = Conversation.builder().id(id).text("Old Text").archived("N").build();
        Conversation updatedConversationDetails = Conversation.builder().text("New Text").archived("Y").build();
        Conversation updatedConversation = Conversation.builder().id(id).text("New Text").archived("Y").build();

        when(conversationRepository.findById(id)).thenReturn(Optional.of(existingConversation));
        when(conversationRepository.save(existingConversation)).thenReturn(updatedConversation);

        // Act
        ResponseEntity<Conversation> response = conversationController.updateConversation(id, updatedConversationDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedConversation, response.getBody());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, times(1)).save(existingConversation);
    }

    @Test
    void testUpdateConversation_NotFound() {
        // Arrange
        Long id = 1L;
        Conversation updatedConversationDetails = Conversation.builder().text("New Text").archived("Y").build();
        when(conversationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Conversation> response = conversationController.updateConversation(id, updatedConversationDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void testDeleteConversation() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<Void> response = conversationController.deleteConversation(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(conversationRepository, times(1)).deleteById(id);
    }

    @Test
    void testArchiveConversation_Found() {
        // Arrange
        Long id = 1L;
        Conversation conversation = Conversation.builder().id(id).text("Text").archived("N").build();
        when(conversationRepository.findById(id)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        // Act
        ResponseEntity<Void> response = conversationController.archiveConversation(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Y", conversation.getArchived());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, times(1)).save(conversation);
    }

    @Test
    void testArchiveConversation_NotFound() {
        // Arrange
        Long id = 1L;
        when(conversationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = conversationController.archiveConversation(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void testUnarchiveConversation_Found() {
        // Arrange
        Long id = 1L;
        Conversation conversation = Conversation.builder().id(id).text("Text").archived("Y").build();
        when(conversationRepository.findById(id)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        // Act
        ResponseEntity<Void> response = conversationController.unarchiveConversation(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("N", conversation.getArchived());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, times(1)).save(conversation);
    }

    @Test
    void testUnarchiveConversation_NotFound() {
        // Arrange
        Long id = 1L;
        when(conversationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = conversationController.unarchiveConversation(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(conversationRepository, times(1)).findById(id);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    /*@Test
    public void testFilterConversations_withAllParameters() {
        // Arrange
        String title = "Test";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String archived = "Y";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("startDate", "2024-01-01");
        requestBody.put("endDate", "2024-01-31");
        requestBody.put("archived", archived);

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferenceTitleContainingAndConferencePlannedDateBetweenAndArchived(title, startDate, endDate, archived))
                .thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withTitleAndDateRange() {
        // Arrange
        String title = "Test";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("startDate", "2024-01-01");
        requestBody.put("endDate", "2024-01-31");

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferenceTitleContainingAndConferencePlannedDateBetween(title, startDate, endDate))
                .thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withTitleAndArchived() {
        // Arrange
        String title = "Test";
        String archived = "Y";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("archived", archived);

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferenceTitleContainingAndArchived(title, archived))
                .thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withDateRangeAndArchived() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String archived = "Y";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("startDate", "2024-01-01");
        requestBody.put("endDate", "2024-01-31");
        requestBody.put("archived", archived);

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferencePlannedDateBetweenAndArchived(startDate, endDate, archived))
                .thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withTitle() {
        // Arrange
        String title = "Test";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferenceTitleContaining(title)).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("startDate", "2024-01-01");
        requestBody.put("endDate", "2024-01-31");

        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByConferencePlannedDateBetween(startDate, endDate)).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withArchived() {
        // Arrange
        String archived = "Y";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("archived", archived);
        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findByArchived(archived)).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testFilterConversations_withNoParameters() {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        List<Conversation> expectedConversations = new ArrayList<>();
        when(conversationRepository.findAll()).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.filterConversations(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }*/

    @Test
    void testGetConversationsByUserId() {

        // Arrange
        Long userId = 1L;
        List<Conversation> expectedConversations = new ArrayList<>();
        Conversation conversation1 = new Conversation();
        conversation1.setId(101L);
        expectedConversations.add(conversation1);
        Conversation conversation2 = new Conversation();
        conversation2.setId(102L);
        expectedConversations.add(conversation2);
        when(conversationRepository.findByUserId(userId)).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<Conversation>> responseEntity = conversationController.getConversationsByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConversations, responseEntity.getBody());
    }

    @Test
    public void testArchiveAllConversationsByConferenceId_Found() throws Exception {
        // Arrange
        Long id = 1L;
        Conversation conversation = Conversation.builder().id(id).text("Text").archived("Y").build();

        // Act
        ResponseEntity<Void> response = conversationController.archiveAllConversation(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Y", conversation.getArchived());
    }

    @Test
    void testArchiveAllConversationsByConferenceId_NotFound() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<Void> response = conversationController.archiveAllConversation(id);

        // Assert
        assertEquals(HttpStatus.OK /* NOT_FOUND */, response.getStatusCode());
    }

}
