package org.springframework.boot.jdbc;

import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.JdbcClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/SpringJdbcDependsOnDatabaseInitializationDetector.class */
class SpringJdbcDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    SpringJdbcDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Set.of(JdbcClient.class, JdbcOperations.class, NamedParameterJdbcOperations.class);
    }
}
