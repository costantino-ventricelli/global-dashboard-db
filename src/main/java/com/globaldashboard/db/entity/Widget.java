package com.globaldashboard.db.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("widgets")
public record Widget(
        @Id @GeneratedValue(GeneratedValue.Type.AUTO) @Nullable Long id,

        Long dashboardId,

        String title,

        String type,

        @Nullable String dataSource,

        @Nullable String configJson,

        Integer positionX,

        Integer positionY,

        Integer width,

        Integer height) {
}
