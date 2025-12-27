package com.globaldashboard.db.consumer;

import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.service.UserService;
import com.globaldashboard.db.producer.UserReplyProducer;
import com.globaldashboard.db.event.user.UserEvent;
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

    @Mock
    private UserReplyProducer replyProducer;

    @InjectMocks
    private UserConsumer userConsumer;

    @Test
    void shouldProcessUserCreateRequest_AndReplySuccess() {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password");

        com.globaldashboard.db.entity.User createdUser = new com.globaldashboard.db.entity.User();
        createdUser.setId(123L);
        createdUser.setUsername("testuser");
        createdUser.setEmail("test@example.com");
        createdUser.setPasswordHash("hashedPwd");

        org.mockito.Mockito.when(userService.createUser(request)).thenReturn(createdUser);

        userConsumer.receive(request, null, Collections.emptyMap());

        verify(userService).createUser(request);
        verify(replyProducer).sendReply(org.mockito.ArgumentMatchers.eq("testuser"),
                org.mockito.ArgumentMatchers.argThat(
                        event -> event.type() == com.globaldashboard.db.event.user.UserEvent.EventType.CREATED &&
                                event.id().equals(123L)));
    }

    @Test
    void shouldProcessUserCreateRequest_AndReplyError() {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password");

        org.mockito.Mockito.when(userService.createUser(request)).thenThrow(new RuntimeException("Email exists"));

        userConsumer.receive(request, null, Collections.emptyMap());

        verify(userService).createUser(request);
        verify(replyProducer).sendReply(org.mockito.ArgumentMatchers.eq("testuser"),
                org.mockito.ArgumentMatchers
                        .argThat(event -> event.type() == com.globaldashboard.db.event.user.UserEvent.EventType.ERROR &&
                                event.message().equals("Email exists")));
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
        verify(replyProducer, never()).sendReply(any(), any());
    }
}
