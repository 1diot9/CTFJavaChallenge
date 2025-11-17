package org.springframework.boot.r2dbc.init;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/init/R2dbcScriptDatabaseInitializerDetector.class */
class R2dbcScriptDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    R2dbcScriptDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(R2dbcScriptDatabaseInitializer.class);
    }
}
