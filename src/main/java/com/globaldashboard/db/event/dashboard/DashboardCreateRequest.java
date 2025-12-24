package com.globaldashboard.db.event.dashboard;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record DashboardCreateRequest(Long userId, String name, String description) {
}
