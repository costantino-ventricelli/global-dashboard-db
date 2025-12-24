package com.globaldashboard.db.event.dashboard;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record WidgetAddRequest(Long dashboardId, String title, WidgetType type, String configJson,
        Integer posX, Integer posY, Integer width, Integer height) {
    public enum WidgetType {
        COUNTRY_INFO, HOLIDAY_WEATHER
    }
}
