package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.Flyway;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayMigrationStrategy.class */
public interface FlywayMigrationStrategy {
    void migrate(Flyway flyway);
}
