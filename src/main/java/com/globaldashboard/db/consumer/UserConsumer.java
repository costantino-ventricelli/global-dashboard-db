package com.globaldashboard.db.consumer;

import com.globaldashboard.db.aop.KafkaMdc;
import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.service.UserService;
import com.globaldashboard.db.producer.UserReplyProducer;
import com.globaldashboard.db.event.user.UserEvent;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaMdc
@KafkaListener(groupId = "global-dashboard-db-users")
public class UserConsumer {

    private final UserService userService;
    private final UserReplyProducer replyProducer;

    public UserConsumer(UserService userService, UserReplyProducer replyProducer) {
        this.userService = userService;
        this.replyProducer = replyProducer;
    }

    @Topic("persistence.users")
    public void receive(UserCreateRequest createRequest, UserFindRequest findRequest,
            @MessageHeader Map<String, byte[]> headers) {
        log.info("Received message from persistence.users");

        if (createRequest != null) {
            try {
                com.globaldashboard.db.entity.User user = userService.createUser(createRequest);

                UserEvent successEvent = new UserEvent(user.getId(), user.getUsername(), user.getEmail(),
                        user.getPasswordHash(),
                        UserEvent.EventType.CREATED, "User created successfully");
                replyProducer.sendReply(createRequest.username(), successEvent);

            } catch (Exception e) {
                log.error("Error creating user", e);
                UserEvent errorEvent = new UserEvent(null, createRequest.username(), null, null,
                        UserEvent.EventType.ERROR, e.getMessage());
                replyProducer.sendReply(createRequest.username(), errorEvent);
            }
        } else if (findRequest != null) {
            userService.findUser(findRequest);
        } else {
            log.warn("Received unknown payload type");
        }
    }
}
