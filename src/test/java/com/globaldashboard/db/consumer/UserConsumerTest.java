package com.globaldashboard.db.consumer;

import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.service.UserService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserConsumerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserConsumer userConsumer;

    @Test
    void shouldProcessUserCreateRequest() {
        UserCreateRequest request =
                new UserCreateRequest("testuser", "test@example.com", "password");
        userConsumer.receive(request, null, Collections.emptyMap());
        verify(userService).createUser(request);
        verify(userService, never()).findUser(any());
    }

    @Test
    void shouldProcessUserFindRequest() {
        UserFindRequest request = new UserFindRequest("testuser");
        userConsumer.receive(null, request, Collections.emptyMap());
        verify(userService).findUser(request);
        verify(userService, never()).createUser(any());
    }

    @Test
    void shouldIgnoreUnknownPayload() {
        userConsumer.receive(null, null, Collections.emptyMap());
        verify(userService, never()).createUser(any());
        verify(userService, never()).findUser(any());
    }
}
