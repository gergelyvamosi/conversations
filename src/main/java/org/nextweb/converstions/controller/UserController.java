package org.nextweb.converstions.controller;

import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return new ResponseEntity<>(user, user.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setName(userDetails.getName());
            User updatedUser = userRepository.save(existingUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @PostMapping(value = "/wrapper", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> createUserWrapper(@RequestBody String text) {
        logger.info("Received text: {}", text);
        try {

            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
            
            logger.info("Parsed data: name={}", jsonObject.get("name").getAsString());

            User newUser = new User();
            newUser.setName(jsonObject.get("name").getAsString());

            User savedUser = userRepository.save(newUser);

            // 4. Return the response with a 200 OK status and the correct content type.
            return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Success");

        } catch (JsonSyntaxException e) {
            logger.error("Invalid JSON received: {}", text, e);
            return ResponseEntity.badRequest()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Error");
        } catch (Exception e) {
            logger.error("Error processing request: {}", text, e);
            return ResponseEntity.internalServerError()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body("Error");
        }
    }
}
