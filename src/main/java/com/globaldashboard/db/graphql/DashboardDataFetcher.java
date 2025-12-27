package com.globaldashboard.db.graphql;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.repository.DashboardRepository;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class DashboardDataFetcher {

    private final DashboardRepository dashboardRepository;

    public DashboardDataFetcher(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DataFetcher<Dashboard> getDashboard() {
        return env -> {
            String id = env.getArgument("id");
            return dashboardRepository.findById(Long.valueOf(id)).orElse(null);
        };
    }

    public DataFetcher<List<Dashboard>> getUserDashboards() {
        return env -> {
            String userId = env.getArgument("userId");
            // Assuming userId is UUID based on previous services. Correction: Entity uses
            // Long for userId?
            // Let's check Dashboard.java again. It has private Long userId;
            // So we parse Long, not UUID.
            return dashboardRepository.findAllByUserId(Long.valueOf(userId));
        };
    }
}
