package org.springframework.boot.jdbc;

import javax.sql.DataSource;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/SchemaManagementProvider.class */
public interface SchemaManagementProvider {
    SchemaManagement getSchemaManagement(DataSource dataSource);
}
