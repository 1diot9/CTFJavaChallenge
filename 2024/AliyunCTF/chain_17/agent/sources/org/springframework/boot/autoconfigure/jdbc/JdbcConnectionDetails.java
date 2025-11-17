package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;
import org.springframework.boot.jdbc.DatabaseDriver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/JdbcConnectionDetails.class */
public interface JdbcConnectionDetails extends ConnectionDetails {
    String getUsername();

    String getPassword();

    String getJdbcUrl();

    default String getDriverClassName() {
        return DatabaseDriver.fromJdbcUrl(getJdbcUrl()).getDriverClassName();
    }

    default String getXaDataSourceClassName() {
        return DatabaseDriver.fromJdbcUrl(getJdbcUrl()).getXaDataSourceClassName();
    }
}
