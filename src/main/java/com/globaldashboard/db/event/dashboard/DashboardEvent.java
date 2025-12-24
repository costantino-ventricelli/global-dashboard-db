package com.globaldashboard.db.event.dashboard;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record DashboardEvent(Long dashboardId, Long userId, String name, List<WidgetDto> widgets,
        EventType type, String message) {
    public enum EventType {
        CREATED, UPDATED, DELETED, ERROR, LIST_RETURNED
    }

    @Serdeable
    public record WidgetDto(Long id, String title, String type) {
    }
}
