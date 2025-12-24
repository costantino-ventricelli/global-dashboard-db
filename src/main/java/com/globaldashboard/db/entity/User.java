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
@MappedEntity("users")
public class User {
        @Id
        @GeneratedValue(GeneratedValue.Type.AUTO)
        private Long id;

        private String username;
        private String email;
        private String passwordHash;
        private Instant createdAt = Instant.now();

        public User(String username, String email, String passwordHash) {
                this.username = username;
                this.email = email;
                this.passwordHash = passwordHash;
                this.createdAt = Instant.now();
        }
}
