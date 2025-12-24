package com.globaldashboard.db.event.user;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UserFindRequest(String username) {
}
