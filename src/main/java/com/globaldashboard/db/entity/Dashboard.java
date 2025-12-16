package com.globaldashboard.db.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

@Serdeable
@MappedEntity("dashboards")
public record Dashboard(
        @Id @GeneratedValue(GeneratedValue.Type.AUTO) @Nullable Long id,

        Long userId,

        String name,

        @Nullable String description,

        @DateCreated Instant createdAt,

        @DateUpdated Instant updatedAt) {
}
