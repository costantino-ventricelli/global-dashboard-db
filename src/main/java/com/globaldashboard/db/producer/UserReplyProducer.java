package com.globaldashboard.db.producer;

import com.globaldashboard.db.event.user.UserEvent;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface UserReplyProducer {
    @Topic("persistence.users.replies")
    void sendReply(@KafkaKey String username, UserEvent event);
}
