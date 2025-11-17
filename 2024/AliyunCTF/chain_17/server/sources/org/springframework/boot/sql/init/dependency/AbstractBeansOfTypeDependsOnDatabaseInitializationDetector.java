package org.springframework.boot.sql.init.dependency;

import java.util.Collections;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/AbstractBeansOfTypeDependsOnDatabaseInitializationDetector.class */
public abstract class AbstractBeansOfTypeDependsOnDatabaseInitializationDetector implements DependsOnDatabaseInitializationDetector {
    protected abstract Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes();

    @Override // org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector
    public Set<String> detect(ConfigurableListableBeanFactory beanFactory) {
        try {
            Set<Class<?>> types = getDependsOnDatabaseInitializationBeanTypes();
            return new BeansOfTypeDetector(types).detect(beanFactory);
        } catch (Throwable th) {
            return Collections.emptySet();
        }
    }
}
