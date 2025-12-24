package com.globaldashboard.db.service;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.event.dashboard.DashboardCreateRequest;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest;
import com.globaldashboard.db.producer.DashboardProducer;
import com.globaldashboard.db.repository.DashboardRepository;
import com.globaldashboard.db.repository.UserRepository;
import com.globaldashboard.db.repository.WidgetRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@MicronautTest
class DashboardServiceIntegrationTest {

    @Inject
    DashboardService dashboardService;

    @Inject
    DashboardRepository dashboardRepository;

    @Inject
    WidgetRepository widgetRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    DashboardProducer dashboardProducer;

    @MockBean(DashboardProducer.class)
    DashboardProducer dashboardProducer() {
        return Mockito.mock(DashboardProducer.class);
    }

    private Long testUserId;

    @BeforeEach
    void setup() {
        widgetRepository.deleteAll();
        dashboardRepository.deleteAll();
        userRepository.deleteAll();

        testUserId =
                userRepository.save(new User("dash_owner", "owner@example.com", "hash")).getId();
    }

    @Test
    void testCreateDashboard() {
        DashboardCreateRequest request = new DashboardCreateRequest(testUserId, "My Dash", "Desc");

        dashboardService.createDashboard(request);

        // Verify DB
        Assertions.assertTrue(dashboardRepository.findAll().iterator().hasNext());
        // Verify Producer
        verify(dashboardProducer).sendEvent(eq(testUserId.toString()), any());
    }

    @Test
    void testAddWidget_Success() {
        // Create Dashboard first
        Dashboard dashboard =
                dashboardRepository.save(new Dashboard(testUserId, "My Dash", "Desc"));
        Long dashId = dashboard.getId();

        WidgetAddRequest request = new WidgetAddRequest(dashId, "Weather Widget",
                WidgetAddRequest.WidgetType.HOLIDAY_WEATHER, "{}", 0, 0, 1, 1);

        dashboardService.addWidget(request);

        // Verify Widget DB
        Assertions.assertTrue(widgetRepository.findAllByDashboardId(dashId).size() > 0);
        // Verify Producer
        verify(dashboardProducer).sendEvent(eq(testUserId.toString()), any());
    }

    @Test
    void testAddWidget_DashboardNotFound() {
        WidgetAddRequest request = new WidgetAddRequest(999L, "Weather Widget",
                WidgetAddRequest.WidgetType.HOLIDAY_WEATHER, "{}", 0, 0, 1, 1);

        dashboardService.addWidget(request);

        // Verify Producer Error (unknown key)
        verify(dashboardProducer).sendEvent(eq("unknown"), any());
    }
}
