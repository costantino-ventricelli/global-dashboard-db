package com.globaldashboard.db.repository;

import com.globaldashboard.db.entity.Dashboard;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DashboardRepository extends CrudRepository<Dashboard, Long> {

    List<Dashboard> findByUserId(Long userId);
}
