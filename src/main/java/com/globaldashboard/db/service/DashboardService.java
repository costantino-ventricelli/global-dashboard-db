package com.globaldashboard.db.service;


import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.entity.Widget;
import com.globaldashboard.db.event.dashboard.DashboardCreateRequest;
import com.globaldashboard.db.event.dashboard.DashboardEvent;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest;
import com.globaldashboard.db.mapper.DashboardMapper;
import com.globaldashboard.db.producer.DashboardProducer;
import com.globaldashboard.db.repository.DashboardRepository;
import com.globaldashboard.db.repository.WidgetRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final WidgetRepository widgetRepository;
    private final DashboardProducer dashboardProducer;
    private final DashboardMapper dashboardMapper;

    public DashboardService(DashboardRepository dashboardRepository,
            WidgetRepository widgetRepository, DashboardProducer dashboardProducer,
            DashboardMapper dashboardMapper) {
        this.dashboardRepository = dashboardRepository;
        this.widgetRepository = widgetRepository;
        this.dashboardProducer = dashboardProducer;
        this.dashboardMapper = dashboardMapper;
    }

    @Transactional
    public void createDashboard(DashboardCreateRequest request) {
        log.info("Creating dashboard '{}' for user {}", request.name(), request.userId());

        Dashboard dashboard = dashboardMapper.toEntity(request);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);

        List<DashboardEvent.WidgetDto> widgets = Collections.emptyList();
        DashboardEvent event = new DashboardEvent(savedDashboard.getId(),
                savedDashboard.getUserId(), savedDashboard.getName(), widgets,
                DashboardEvent.EventType.CREATED, "Dashboard created");

        dashboardProducer.sendEvent(savedDashboard.getUserId().toString(), event);
    }

    @Transactional
    public void addWidget(WidgetAddRequest request) {
        log.info("Adding widget '{}' to dashboard {}", request.title(), request.dashboardId());
        Optional<Dashboard> dashboardOpt = dashboardRepository.findById(request.dashboardId());

        if (dashboardOpt.isEmpty()) {
            log.error("Dashboard not found: {}", request.dashboardId());
            dashboardProducer.sendEvent("unknown", new DashboardEvent(request.dashboardId(), null,
                    null, null, DashboardEvent.EventType.ERROR, "Dashboard not found"));
            return;
        }

        Dashboard dashboard = dashboardOpt.get();
        Widget widget = dashboardMapper.toEntity(request);
        widgetRepository.save(widget);

        List<Widget> widgets = widgetRepository.findAllByDashboardId(dashboard.getId());
        List<DashboardEvent.WidgetDto> widgetDtos =
                widgets.stream().map(dashboardMapper::toWidgetDto).collect(Collectors.toList());

        DashboardEvent event = new DashboardEvent(dashboard.getId(), dashboard.getUserId(),
                dashboard.getName(), widgetDtos, DashboardEvent.EventType.UPDATED, "Widget added");

        dashboardProducer.sendEvent(dashboard.getUserId().toString(), event);
    }
}
