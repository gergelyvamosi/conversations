package org.nextweb.converstions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nextweb.converstions.model.User;
import org.nextweb.converstions.repository.UserRepository;
import org.nextweb.converstions.util.UserHelper;


@SpringBootApplication
public class ConferenceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConferenceManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository) {
        return args -> {
            // Create initial users
            if (userRepository.count() == 0) {
                User user1 = User.builder().name(UserHelper.ROOT_USER).build();
                User user2 = User.builder().name("john").build();
                User user3 = User.builder().name("greg").build();
                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
            }
        };
    }

}