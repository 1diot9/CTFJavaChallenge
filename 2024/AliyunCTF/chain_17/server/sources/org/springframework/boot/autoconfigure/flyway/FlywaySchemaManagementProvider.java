package org.springframework.boot.autoconfigure.flyway;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.SchemaManagement;
import org.springframework.boot.jdbc.SchemaManagementProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywaySchemaManagementProvider.class */
class FlywaySchemaManagementProvider implements SchemaManagementProvider {
    private final Iterable<Flyway> flywayInstances;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlywaySchemaManagementProvider(Iterable<Flyway> flywayInstances) {
        this.flywayInstances = flywayInstances;
    }

    @Override // org.springframework.boot.jdbc.SchemaManagementProvider
    public SchemaManagement getSchemaManagement(DataSource dataSource) {
        Stream map = StreamSupport.stream(this.flywayInstances.spliterator(), false).map(flyway -> {
            return flyway.getConfiguration().getDataSource();
        });
        Objects.requireNonNull(dataSource);
        return (SchemaManagement) map.filter((v1) -> {
            return r1.equals(v1);
        }).findFirst().map(managedDataSource -> {
            return SchemaManagement.MANAGED;
        }).orElse(SchemaManagement.UNMANAGED);
    }
}
