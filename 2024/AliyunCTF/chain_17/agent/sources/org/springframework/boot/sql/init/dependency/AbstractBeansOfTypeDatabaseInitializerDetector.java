package org.springframework.boot.sql.init.dependency;

import java.util.Collections;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/AbstractBeansOfTypeDatabaseInitializerDetector.class */
public abstract class AbstractBeansOfTypeDatabaseInitializerDetector implements DatabaseInitializerDetector {
    protected abstract Set<Class<?>> getDatabaseInitializerBeanTypes();

    @Override // org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector
    public Set<String> detect(ConfigurableListableBeanFactory beanFactory) {
        try {
            Set<Class<?>> types = getDatabaseInitializerBeanTypes();
            return new BeansOfTypeDetector(types).detect(beanFactory);
        } catch (Throwable th) {
            return Collections.emptySet();
        }
    }
}
