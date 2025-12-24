package com.globaldashboard.db.consumer;

import com.globaldashboard.db.aop.KafkaMdc;
import com.globaldashboard.db.event.dashboard.DashboardCreateRequest;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest;
import com.globaldashboard.db.service.DashboardService;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaMdc
@KafkaListener(groupId = "global-dashboard-db-dashboards")
public class DashboardConsumer {

    private final DashboardService dashboardService;

    public DashboardConsumer(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Topic("persistence.dashboards")
    public void receive(DashboardCreateRequest createRequest, WidgetAddRequest widgetAddRequest,
            @MessageHeader Map<String, byte[]> headers) {
        log.info("Received message from persistence.dashboards");

        if (createRequest != null) {
            dashboardService.createDashboard(createRequest);
        } else if (widgetAddRequest != null) {
            dashboardService.addWidget(widgetAddRequest);
        } else {
            log.warn("Received unknown payload type");
        }
    }
}
