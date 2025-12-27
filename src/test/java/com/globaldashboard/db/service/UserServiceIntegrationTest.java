package com.globaldashboard.db.service;

import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.producer.UserProducer;
import com.globaldashboard.db.repository.UserRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.globaldashboard.db.AbstractIntegrationTest;

@MicronautTest
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    UserProducer userProducer;

    @MockBean(UserProducer.class)
    UserProducer userProducer() {
        return Mockito.mock(UserProducer.class);
    }

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_Success() {
        UserCreateRequest request = new UserCreateRequest("newuser", "new@example.com", "secret");

        userService.createUser(request);

        // Verify DB
        Assertions.assertTrue(userRepository.findByUsername("newuser").isPresent());
        // Verify Producer
        verify(userProducer).sendEvent(eq("newuser"), any());
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        // Setup existing
        userRepository.save(new User("existing", "duplicate@example.com", "hash"));

        UserCreateRequest request = new UserCreateRequest("newuser", "duplicate@example.com", "secret");

        userService.createUser(request);

        // Verify NO new user created (username mismatch)
        Assertions.assertTrue(userRepository.findByUsername("newuser").isEmpty());
        // Verify Error Event sent
        verify(userProducer).sendEvent(eq("duplicate@example.com"), any());
    }

    @Test
    void testFindUser_Success() {
        userRepository.save(new User("findme", "find@example.com", "hash"));
        UserFindRequest request = new UserFindRequest("findme");

        userService.findUser(request);

        verify(userProducer).sendEvent(eq("findme"), any());
    }

    @Test
    void testFindUser_NotFound() {
        UserFindRequest request = new UserFindRequest("unknown");

        userService.findUser(request);

        verify(userProducer).sendEvent(eq("unknown"), any());
    }
}
