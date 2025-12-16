package com.globaldashboard.db.repository;

import com.globaldashboard.db.entity.Widget;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WidgetRepository extends CrudRepository<Widget, Long> {

    List<Widget> findByDashboardId(Long dashboardId);
}
