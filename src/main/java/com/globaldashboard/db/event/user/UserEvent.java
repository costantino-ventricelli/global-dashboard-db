package com.globaldashboard.db.event.user;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Generic event on 'persistence.users.events' (Success/Error).
 */
@Serdeable
public record UserEvent(Long id, String username, String email, EventType type, String message) {
    public enum EventType {
        CREATED, FOUND, NOT_FOUND, ERROR
    }
}
