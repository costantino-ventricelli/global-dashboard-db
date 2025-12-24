package com.globaldashboard.db.infrastructure.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.MDC;

public class MdcPropagationInterceptor implements ProducerInterceptor<Object, Object> {

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        if (contextMap != null) {
            contextMap.forEach((key, value) -> {
                if (value != null) {
                    record.headers().add(key, value.getBytes(StandardCharsets.UTF_8));
                }
            });
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // No-op
    }

    @Override
    public void close() {
        // No-op
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // No-op
    }
}
