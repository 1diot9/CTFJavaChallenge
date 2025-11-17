package org.springframework.boot.sql.init.dependency;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/AnnotationDependsOnDatabaseInitializationDetector.class */
class AnnotationDependsOnDatabaseInitializationDetector implements DependsOnDatabaseInitializationDetector {
    AnnotationDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector
    public Set<String> detect(ConfigurableListableBeanFactory beanFactory) {
        Set<String> dependentBeans = new HashSet<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            if (beanFactory.findAnnotationOnBean(beanName, DependsOnDatabaseInitialization.class, false) != null) {
                dependentBeans.add(beanName);
            }
        }
        return dependentBeans;
    }
}
