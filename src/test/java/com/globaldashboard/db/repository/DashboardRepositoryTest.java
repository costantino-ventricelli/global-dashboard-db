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

import com.globaldashboard.db.AbstractIntegrationTest;

@MicronautTest
class DashboardRepositoryTest extends AbstractIntegrationTest {

    @Inject
    DashboardRepository dashboardRepository;

    @Inject
    UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setup() {
        // Dashboard needs a User
        User user = new User("dash_owner", "owner@example.com", "hash");
        testUserId = userRepository.save(user).getId();
    }

    @AfterEach
    void cleanup() {
        dashboardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testDashboardCrud() {
        // 1. Create
        Dashboard dashboard = new Dashboard(testUserId, "My Dashboard", "A test dashboard");
        Dashboard saved = dashboardRepository.save(dashboard);

        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("My Dashboard", saved.getName());
        Assertions.assertEquals(testUserId, saved.getUserId());

        // 2. Read
        Optional<Dashboard> found = dashboardRepository.findById(saved.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("A test dashboard", found.get().getDescription());

        // 3. Update (Entities are mutable)
        found.get().setName("Updated Name");
        Dashboard updated = dashboardRepository.update(found.get());
        Assertions.assertEquals("Updated Name", updated.getName());

        // 4. Delete
        dashboardRepository.deleteById(saved.getId());
        Assertions.assertTrue(dashboardRepository.findById(saved.getId()).isEmpty());
    }
}
