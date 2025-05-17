package org.nextweb.converstions.controller;

import org.nextweb.converstions.model.Conference;
import org.nextweb.converstions.model.Conversation;
import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.ConferenceRepository;
import org.nextweb.converstions.repository.ConversationRepository;
import org.nextweb.converstions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        List<Conversation> conversations = conversationRepository.findAll();
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/conference/{id}")
    public ResponseEntity<List<Conversation>> getAllConversationsByConferenceId(@PathVariable Long id) {
        List<Conversation> conversations = conversationRepository.findByConferenceId(id);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Conversation>> getConversationById(@PathVariable Long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        return new ResponseEntity<>(conversation, conversation.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        Conversation savedConversation = conversationRepository.save(conversation);
        return new ResponseEntity<>(savedConversation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conversation> updateConversation(@PathVariable Long id, @RequestBody Conversation conversationDetails) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isPresent()) {
            Conversation existingConversation = conversation.get();
            existingConversation.setUserA(conversationDetails.getUserA());
            existingConversation.setUserB(conversationDetails.getUserB());
            existingConversation.setText(conversationDetails.getText());
            existingConversation.setArchived(conversationDetails.getArchived());
            Conversation updatedConversation = conversationRepository.save(existingConversation);
            return new ResponseEntity<>(updatedConversation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        conversationRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveConversation(@PathVariable Long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isPresent()) {
            conversation.get().archive();
            conversationRepository.save(conversation.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveConversation(@PathVariable Long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isPresent()) {
            conversation.get().unarchive();
            conversationRepository.save(conversation.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Conversation>> filterConversations(
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "archived", required = false) String archived) {

        String _title = ("".equals(title)?null:title);
        LocalDate _startDate = null;
        LocalDate _endDate = null;
        String _archived = ("".equals(archived)?null:archived);

        if(startDate != null && ! "".equals(startDate)) {
           _startDate = LocalDate.parse(startDate);
        }

        if(endDate != null && ! "".equals(endDate)) {
           _endDate = LocalDate.parse(endDate);
        }
       

        List<Conversation> conversations;

       if (_title != null && _startDate != null && _endDate != null && _archived != null) {
            conversations = conversationRepository.findByConferenceTitleContainingAndConferencePlannedDateBetweenAndArchived(_title, _startDate, _endDate, _archived);
        } else if (_title != null && _startDate != null && _endDate != null) {
            conversations = conversationRepository.findByConferenceTitleContainingAndConferencePlannedDateBetween(_title, _startDate, _endDate);
        } else if (_title != null && _archived != null) {
            conversations = conversationRepository.findByConferenceTitleContainingAndArchived(_title, _archived);
        } else if (_startDate != null && _endDate != null && _archived != null) {
            conversations = conversationRepository.findByConferencePlannedDateBetweenAndArchived(_startDate, _endDate, _archived);
        }else if (_title != null) {
            conversations = conversationRepository.findByConferenceTitleContaining(_title);
        } else if (_startDate != null && _endDate != null) {
            conversations = conversationRepository.findByConferencePlannedDateBetween(_startDate, _endDate);
        } else if (_archived != null) {
            conversations = conversationRepository.findByArchived(_archived);
        } else {
            conversations = conversationRepository.findAll();
        }

        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Conversation>> getConversationsByUserId(@PathVariable Long userId) { 
        List<Conversation> conversations = conversationRepository.findByUserId(userId);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @PutMapping("/archive/{conferenceId}")
    public ResponseEntity<Void> archiveAllConversation(@PathVariable Long conferenceId) {
        try {
            conversationRepository.archiveByConferenceId(conferenceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/wrapper", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> createConversationWrapper(@RequestBody String text) {

        try {

            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

            Optional<User> userA = userRepository.findById(jsonObject.get("userA").getAsJsonObject().get("id").getAsNumber().longValue());
            Optional<User> userB = userRepository.findById(jsonObject.get("userB").getAsJsonObject().get("id").getAsNumber().longValue());
            Optional<Conference> conference = conferenceRepository.findById(jsonObject.get("conference").getAsJsonObject().get("id").getAsNumber().longValue());

            if (userA.isPresent() && userB.isPresent() && conference.isPresent()) {
                Conversation newConversation = new Conversation();
                newConversation.setUserA(userA.get());
                newConversation.setUserB(userB.get());
                newConversation.setConference(conference.get());
                newConversation.setText(jsonObject.get("text").getAsString());

                Conversation savedConversation = conversationRepository.save(newConversation);
            }

            // 4. Return the response with a 200 OK status and the correct content type.
            return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Success");

        } catch (JsonSyntaxException e) {
            return ResponseEntity.badRequest()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Error");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Error");
        }
    }

    @PutMapping(value = "/wrapper/{id}", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> updateConversationWrapper(@PathVariable Long id, @RequestBody String text) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isPresent()) {

            Conversation existingConversation = conversation.get();

            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

            existingConversation.setText(jsonObject.get("text").getAsString());
            Conversation updatedConversation = conversationRepository.save(existingConversation);

            return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Success");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
