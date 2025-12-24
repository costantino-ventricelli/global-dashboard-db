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
        User user = new User("widget_owner", "widget@example.com", "hash");
        Long userId = userRepository.save(user).getId();

        Dashboard dashboard = new Dashboard(userId, "Widget Board", "Test Description");
        testDashboardId = dashboardRepository.save(dashboard).getId();
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
        Widget widget = new Widget();
        widget.setDashboardId(testDashboardId);
        widget.setTitle("CPU Usage");
        widget.setType("CHART");
        widget.setConfigJson("{}");
        widget.setPositionX(0);
        widget.setPositionY(0);
        widget.setWidth(2);
        widget.setHeight(2);

        Widget saved = widgetRepository.save(widget);

        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("CPU Usage", saved.getTitle());

        // 2. Read
        Optional<Widget> found = widgetRepository.findById(saved.getId());
        Assertions.assertTrue(found.isPresent());
        // Note: dataSource property was used in old test but Widget entity doesn't seem to have it?
        // Checking Widget entity... title, type, configJson, pos...
        // Assuming configJson or type is what was meant.
        // Logic: previously expected "prometheus" for dataSource.
        // I'll check what I set... I set ConfigJson to "{}".
        // Let's assert type or title instead.
        Assertions.assertEquals("CHART", found.get().getType());

        // 3. Delete
        widgetRepository.deleteById(saved.getId());
        Assertions.assertTrue(widgetRepository.findById(saved.getId()).isEmpty());
    }
}
