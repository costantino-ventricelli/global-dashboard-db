package com.globaldashboard.db.mapper;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.entity.Widget;
import com.globaldashboard.db.event.dashboard.DashboardCreateRequest;
import com.globaldashboard.db.event.dashboard.DashboardEvent;
import com.globaldashboard.db.event.dashboard.WidgetAddRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "jsr330")
public interface DashboardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    Dashboard toEntity(DashboardCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dashboardId", source = "dashboardId")
    @Mapping(target = "positionX", source = "posX")
    @Mapping(target = "positionY", source = "posY")
    @Mapping(target = "type", expression = "java(request.type().name())")
    Widget toEntity(WidgetAddRequest request);

    @Mapping(target = "dashboardId", source = "dashboard.id")
    @Mapping(target = "userId", source = "dashboard.userId")
    @Mapping(target = "name", source = "dashboard.name")
    @Mapping(target = "widgets", source = "widgets")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "message", ignore = true)
    DashboardEvent toEvent(Dashboard dashboard, List<DashboardEvent.WidgetDto> widgets);

    DashboardEvent.WidgetDto toWidgetDto(Widget widget);
}
