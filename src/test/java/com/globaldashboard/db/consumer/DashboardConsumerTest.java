package com.globaldashboard.db.consumer;

import com.globaldashboard.db.event.dashboard.DashboardCreateRequest;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest.WidgetType;
import com.globaldashboard.db.service.DashboardService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DashboardConsumerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardConsumer dashboardConsumer;

    @Test
    void shouldProcessDashboardCreateRequest() {
        DashboardCreateRequest request =
                new DashboardCreateRequest(123L, "My Dashboard", "Description");
        dashboardConsumer.receive(request, null, Collections.emptyMap());
        verify(dashboardService).createDashboard(request);
        verify(dashboardService, never()).addWidget(any());
    }

    @Test
    void shouldProcessWidgetAddRequest() {
        WidgetAddRequest request = new WidgetAddRequest(123L, "London Weather",
                WidgetType.HOLIDAY_WEATHER, "{ \"city\": \"London\" }", 0, 0, 2, 2);
        dashboardConsumer.receive(null, request, Collections.emptyMap());
        verify(dashboardService).addWidget(request);
        verify(dashboardService, never()).createDashboard(any());
    }

    @Test
    void shouldIgnoreUnknownPayload() {
        dashboardConsumer.receive(null, null, Collections.emptyMap());
        verify(dashboardService, never()).createDashboard(any());
        verify(dashboardService, never()).addWidget(any());
    }
}
