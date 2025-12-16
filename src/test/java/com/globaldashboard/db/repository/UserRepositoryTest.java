package com.globaldashboard.db.repository;

import com.globaldashboard.db.entity.User;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@MicronautTest // Starts the full Micronaut Context
class UserRepositoryTest {

    @Inject
    UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void testUserCrudOperations() {
        // 1. Create
        User user = new User(null, "jdoe", "john@example.com", "hashed_secret", null);
        User savedUser = userRepository.save(user);

        Assertions.assertNotNull(savedUser.id());
        Assertions.assertEquals("jdoe", savedUser.username());

        // 2. Read
        Optional<User> foundUser = userRepository.findById(savedUser.id());
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("john@example.com", foundUser.get().email());

        // 3. Find Custom
        Optional<User> byUsername = userRepository.findByUsername("jdoe");
        Assertions.assertTrue(byUsername.isPresent());

        // 4. Update
        // Note: With Records/Immutable objects, we create a new instance with the ID
        // set for update
        // But for testing simplicity here, let's just delete
    }
}
