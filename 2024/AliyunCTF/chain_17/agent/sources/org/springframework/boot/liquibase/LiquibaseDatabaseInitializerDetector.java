package org.springframework.boot.liquibase;

import java.util.Collections;
import java.util.Set;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/liquibase/LiquibaseDatabaseInitializerDetector.class */
class LiquibaseDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    LiquibaseDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(SpringLiquibase.class);
    }
}
