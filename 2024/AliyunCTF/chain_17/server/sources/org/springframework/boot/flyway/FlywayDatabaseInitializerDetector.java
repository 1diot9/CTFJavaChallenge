package org.springframework.boot.flyway;

import java.util.Collections;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/flyway/FlywayDatabaseInitializerDetector.class */
class FlywayDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    FlywayDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(Flyway.class);
    }
}
