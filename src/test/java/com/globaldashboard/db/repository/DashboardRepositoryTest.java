package com.globaldashboard.db.repository;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.entity.User;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@MicronautTest
class DashboardRepositoryTest {

    @Inject
    DashboardRepository dashboardRepository;

    @Inject
    UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setup() {
        // Dashboard needs a User
        User user = new User(null, "dash_owner", "owner@example.com", "hash", null);
        testUserId = userRepository.save(user).id();
    }

    @AfterEach
    void cleanup() {
        dashboardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testDashboardCrud() {
        // 1. Create
        Dashboard dashboard = new Dashboard(null, testUserId, "My Dashboard", "A test dashboard", null, null);
        Dashboard saved = dashboardRepository.save(dashboard);

        Assertions.assertNotNull(saved.id());
        Assertions.assertEquals("My Dashboard", saved.name());
        Assertions.assertEquals(testUserId, saved.userId());

        // 2. Read
        Optional<Dashboard> found = dashboardRepository.findById(saved.id());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("A test dashboard", found.get().description());

        // 3. Update (Records are immutable, save new instance with same ID)
        Dashboard toUpdate = new Dashboard(saved.id(), testUserId, "Updated Name", "Updated Desc", saved.createdAt(),
                null);
        Dashboard updated = dashboardRepository.update(toUpdate);
        Assertions.assertEquals("Updated Name", updated.name());

        // 4. Delete
        dashboardRepository.deleteById(saved.id());
        Assertions.assertTrue(dashboardRepository.findById(saved.id()).isEmpty());
    }
}
