package com.globaldashboard.db.consumer;

import com.globaldashboard.db.aop.KafkaMdc;
import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.service.UserService;
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

    public UserConsumer(UserService userService) {
        this.userService = userService;
    }

    @Topic("persistence.users")
    public void receive(UserCreateRequest createRequest, UserFindRequest findRequest,
            @MessageHeader Map<String, byte[]> headers) {
        log.info("Received message from persistence.users");

        if (createRequest != null) {
            userService.createUser(createRequest);
        } else if (findRequest != null) {
            userService.findUser(findRequest);
        } else {
            log.warn("Received unknown payload type");
        }
    }
}
