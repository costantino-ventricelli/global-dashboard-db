package com.globaldashboard.db.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

@Serdeable
@MappedEntity("users")
public record User(
        @Id @GeneratedValue(GeneratedValue.Type.AUTO) @Nullable Long id,

        String username,

        String email,

        String passwordHash,

        @DateCreated Instant createdAt) {
}
