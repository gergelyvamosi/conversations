package org.nextweb.converstions.controller;

import org.nextweb.converstions.model.Conference;
import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.ConferenceRepository;
import org.nextweb.converstions.repository.UserRepository;
import org.nextweb.converstions.util.SystemLogger;
import org.nextweb.converstions.util.UserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/conferences")
public class ConferenceController {
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Conference>> getAllConferences() {
        String username = UserHelper.getAuthenticatedUsername();  // Get username
        SystemLogger.log("username ConferenceController.getAllConferences()", username);
        if (UserHelper.ROOT_USER.equals(username)) {
            List<Conference> conferences = conferenceRepository.findAll();
            return new ResponseEntity<>(conferences, HttpStatus.OK);
        } else {
            List<Conference> conferences = conferenceRepository.findByUsersId(userRepository.findByName(username).get().getId());
            return new ResponseEntity<>(conferences, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Conference>> getConferenceById(@PathVariable Long id) {
        Optional<Conference> conference = conferenceRepository.findById(id);
        return new ResponseEntity<>(conference, conference.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Conference> createConference(@RequestBody Conference conference) {
        Conference savedConference = conferenceRepository.save(conference);
        return new ResponseEntity<>(savedConference, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conference> updateConference(@PathVariable Long id, @RequestBody Conference conferenceDetails) {
        Optional<Conference> conference = conferenceRepository.findById(id);
        if (conference.isPresent()) {
            Conference existingConference = conference.get();
            existingConference.setTitle(conferenceDetails.getTitle());
            existingConference.setPlannedDate(conferenceDetails.getPlannedDate());
            existingConference.setDescription(conferenceDetails.getDescription());
            existingConference.setLocation(conferenceDetails.getLocation());
            Conference updatedConference = conferenceRepository.save(existingConference);
            return new ResponseEntity<>(updatedConference, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConference(@PathVariable Long id) {
        conferenceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static final Logger logger = LoggerFactory.getLogger(ConferenceController.class);

    @PostMapping(value = "/add/{conferenceId}/users/{userId}", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<Void> addUserToConference(@PathVariable Long conferenceId, @PathVariable Long userId) {

        logger.info(conferenceId + " - " + userId);

        Optional<Conference> conference = conferenceRepository.findById(conferenceId);
        Optional<User> user = userRepository.findById(userId);
        if (conference.isPresent() && user.isPresent()) {
            logger.info(conference.get().getTitle() + " - " + user.get().getName());
            conference.get().addUser(user.get());
            conferenceRepository.save(conference.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/remove/{conferenceId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromConference(@PathVariable Long conferenceId, @PathVariable Long userId) {
        Optional<Conference> conference = conferenceRepository.findById(conferenceId);
        Optional<User> user = userRepository.findById(userId);
        if (conference.isPresent() && user.isPresent()) {
            conference.get().removeUser(user.get());
            conferenceRepository.save(conference.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/wrapper", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> createConferenceWrapper(@RequestBody String text) {
        try {

            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

            Conference newConference = new Conference();
            newConference.setTitle(jsonObject.get("title").getAsString());
            newConference.setPlannedDate(LocalDate.parse((jsonObject.get("plannedDate").getAsString())));
            newConference.setDescription(jsonObject.get("description").getAsString());
            newConference.setLocation(jsonObject.get("location").getAsString());

            Conference savedConference = conferenceRepository.save(newConference);

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
    public ResponseEntity<String> updateConferenceWrapper(@PathVariable Long id, @RequestBody String text) {
        Optional<Conference> conference = conferenceRepository.findById(id);
        if (conference.isPresent()) {
            Conference existingConference = conference.get();

            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

            existingConference.setTitle(jsonObject.get("title").getAsString());
            existingConference.setPlannedDate(LocalDate.parse((jsonObject.get("plannedDate").getAsString())));
            existingConference.setDescription(jsonObject.get("description").getAsString());
            existingConference.setLocation(jsonObject.get("location").getAsString());

            Conference updatedConference = conferenceRepository.save(existingConference);
            return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Success");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}