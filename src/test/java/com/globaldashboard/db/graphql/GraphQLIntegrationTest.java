package com.globaldashboard.db.graphql;

import com.globaldashboard.db.entity.Dashboard;
import com.globaldashboard.db.repository.DashboardRepository;
import com.globaldashboard.db.AbstractIntegrationTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(transactional = false)
class GraphQLIntegrationTest extends AbstractIntegrationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    DashboardRepository dashboardRepository;

    @Test
    void testGetDashboard() {
        // Arrange
        Dashboard dashboard = new Dashboard(100L, "My Dashboard", "Description");
        dashboard = dashboardRepository.save(dashboard);

        // Act
        // GraphQL Query: { getDashboard(id: "...") { name } }
        String query = String.format("{\"query\": \"{ getDashboard(id: \\\"%d\\\") { name } }\"}", dashboard.getId());

        HttpRequest<String> request = HttpRequest.POST("/graphql", query);
        Map<String, Object> result = client.toBlocking().retrieve(request, Map.class);

        // Assert
        // Response structure: { data: { getDashboard: { name: "My Dashboard" } } }
        assertNotNull(result);
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        Map<String, Object> dashboardData = (Map<String, Object>) data.get("getDashboard");

        assertEquals("My Dashboard", dashboardData.get("name"));
    }
}
