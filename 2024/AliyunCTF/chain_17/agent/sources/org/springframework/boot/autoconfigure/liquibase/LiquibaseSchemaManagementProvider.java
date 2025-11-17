package org.springframework.boot.autoconfigure.liquibase;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.SchemaManagement;
import org.springframework.boot.jdbc.SchemaManagementProvider;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseSchemaManagementProvider.class */
class LiquibaseSchemaManagementProvider implements SchemaManagementProvider {
    private final Iterable<SpringLiquibase> liquibaseInstances;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LiquibaseSchemaManagementProvider(ObjectProvider<SpringLiquibase> liquibases) {
        this.liquibaseInstances = liquibases;
    }

    @Override // org.springframework.boot.jdbc.SchemaManagementProvider
    public SchemaManagement getSchemaManagement(DataSource dataSource) {
        Stream map = StreamSupport.stream(this.liquibaseInstances.spliterator(), false).map((v0) -> {
            return v0.getDataSource();
        });
        Objects.requireNonNull(dataSource);
        return (SchemaManagement) map.filter((v1) -> {
            return r1.equals(v1);
        }).findFirst().map(managedDataSource -> {
            return SchemaManagement.MANAGED;
        }).orElse(SchemaManagement.UNMANAGED);
    }
}
