package com.globaldashboard.db.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedEntity("dashboards")
public class Dashboard {
        @Id
        @GeneratedValue(GeneratedValue.Type.AUTO)
        private Long id;

        private Long userId;
        private String name;
        private String description;
        private Instant createdAt = Instant.now();
        private Instant updatedAt = Instant.now();

        public Dashboard(Long userId, String name, String description) {
                this.userId = userId;
                this.name = name;
                this.description = description;
                this.createdAt = Instant.now();
                this.updatedAt = Instant.now();
        }
}
