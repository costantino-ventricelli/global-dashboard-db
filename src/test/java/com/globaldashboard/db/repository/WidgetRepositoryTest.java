package com.globaldashboard.db.repository;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.entity.Widget;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@MicronautTest
class WidgetRepositoryTest {

    @Inject
    WidgetRepository widgetRepository;

    @Inject
    DashboardRepository dashboardRepository;

    @Inject
    UserRepository userRepository;

    private Long testDashboardId;

    @BeforeEach
    void setup() {
        User user = new User(null, "widget_owner", "widget@example.com", "hash", null);
        Long userId = userRepository.save(user).id();

        Dashboard dashboard = new Dashboard(null, userId, "Widget Board", null, null, null);
        testDashboardId = dashboardRepository.save(dashboard).id();
    }

    @AfterEach
    void cleanup() {
        widgetRepository.deleteAll();
        dashboardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testWidgetCrud() {
        // 1. Create
        Widget widget = new Widget(null, testDashboardId, "CPU Usage", "CHART", "prometheus", "{}", 0, 0, 2, 2);
        Widget saved = widgetRepository.save(widget);

        Assertions.assertNotNull(saved.id());
        Assertions.assertEquals("CPU Usage", saved.title());

        // 2. Read
        Optional<Widget> found = widgetRepository.findById(saved.id());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("prometheus", found.get().dataSource());

        // 3. Delete
        widgetRepository.deleteById(saved.id());
        Assertions.assertTrue(widgetRepository.findById(saved.id()).isEmpty());
    }
}
