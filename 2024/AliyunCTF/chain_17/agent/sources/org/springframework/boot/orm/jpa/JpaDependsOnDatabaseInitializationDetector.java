package org.springframework.boot.orm.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/orm/jpa/JpaDependsOnDatabaseInitializationDetector.class */
class JpaDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    private final Environment environment;

    JpaDependsOnDatabaseInitializationDetector(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        boolean postpone = ((Boolean) this.environment.getProperty("spring.jpa.defer-datasource-initialization", Boolean.TYPE, false)).booleanValue();
        return postpone ? Collections.emptySet() : new HashSet(Arrays.asList(EntityManagerFactory.class, AbstractEntityManagerFactoryBean.class));
    }
}
