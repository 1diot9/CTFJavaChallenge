package org.springframework.boot.autoconfigure.flyway;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayMigrationInitializerDatabaseInitializerDetector.class */
class FlywayMigrationInitializerDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    FlywayMigrationInitializerDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(FlywayMigrationInitializer.class);
    }

    @Override // org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector, org.springframework.core.Ordered
    public int getOrder() {
        return 1;
    }
}
