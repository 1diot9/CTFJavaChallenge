package org.springframework.boot.jdbc.init;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/init/DataSourceScriptDatabaseInitializerDetector.class */
class DataSourceScriptDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    static final int PRECEDENCE = 2147483547;

    DataSourceScriptDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(DataSourceScriptDatabaseInitializer.class);
    }

    @Override // org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector, org.springframework.core.Ordered
    public int getOrder() {
        return PRECEDENCE;
    }
}
