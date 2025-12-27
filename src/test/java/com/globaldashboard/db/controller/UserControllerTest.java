package com.globaldashboard.db.controller;

import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.repository.UserRepository;
import com.globaldashboard.db.AbstractIntegrationTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactional = false)
class UserControllerTest extends AbstractIntegrationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    UserRepository userRepository;

    @Test
    void testFindUser() {
        // Arrange
        User user = new User("testuser", "test@example.com", "hashedpassword");
        userRepository.save(user);

        // Act
        HttpRequest<Object> request = HttpRequest.GET("/users/testuser");
        User result = client.toBlocking().retrieve(request, User.class);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("hashedpassword", result.getPasswordHash());
    }
}
