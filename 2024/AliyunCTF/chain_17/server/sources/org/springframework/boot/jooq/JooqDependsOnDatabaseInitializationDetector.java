package org.springframework.boot.jooq;

import java.util.Collections;
import java.util.Set;
import org.jooq.DSLContext;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jooq/JooqDependsOnDatabaseInitializationDetector.class */
class JooqDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    JooqDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Collections.singleton(DSLContext.class);
    }
}
