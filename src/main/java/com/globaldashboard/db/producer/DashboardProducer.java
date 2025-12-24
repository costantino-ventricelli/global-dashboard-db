package com.globaldashboard.db.producer;

import com.globaldashboard.db.event.dashboard.DashboardEvent;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface DashboardProducer {
    @Topic("${kafka.topics.dashboards.output}")
    void sendEvent(@KafkaKey String key, DashboardEvent event);
}
