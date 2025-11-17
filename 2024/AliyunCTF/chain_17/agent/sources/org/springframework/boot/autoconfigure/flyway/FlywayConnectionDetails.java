package org.springframework.boot.autoconfigure.flyway;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;
import org.springframework.boot.jdbc.DatabaseDriver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayConnectionDetails.class */
public interface FlywayConnectionDetails extends ConnectionDetails {
    String getUsername();

    String getPassword();

    String getJdbcUrl();

    default String getDriverClassName() {
        String jdbcUrl = getJdbcUrl();
        if (jdbcUrl != null) {
            return DatabaseDriver.fromJdbcUrl(jdbcUrl).getDriverClassName();
        }
        return null;
    }
}
