package com.globaldashboard.db.event.common;

import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.UUID;

/**
 * Metadata carried by every event for auditing, tracing, and diagnosis.
 */
@Serdeable
public record EventMetadata(String correlationId, String spanId, String userId, String clientIp,
        String userAgent, String source, String action, String version, Instant timestamp) {
    public static final String VERSION_1_0 = "1.0";
    public static final String SOURCE_DB = "global-dashboard-db";

    public static EventMetadata create(String correlationId, String userId, String clientIp,
            String action) {
        return new EventMetadata(correlationId, UUID.randomUUID().toString(), // New spanId for this
                                                                              // event
                userId, clientIp, null, SOURCE_DB, action, VERSION_1_0, Instant.now());
    }
}
