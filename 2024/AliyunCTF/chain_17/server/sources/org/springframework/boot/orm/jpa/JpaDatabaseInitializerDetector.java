package org.springframework.boot.orm.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;
import org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/orm/jpa/JpaDatabaseInitializerDetector.class */
class JpaDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    private final Environment environment;

    JpaDatabaseInitializerDetector(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        boolean deferred = ((Boolean) this.environment.getProperty("spring.jpa.defer-datasource-initialization", Boolean.TYPE, false)).booleanValue();
        return deferred ? Collections.singleton(EntityManagerFactory.class) : Collections.emptySet();
    }

    @Override // org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector
    public void detectionComplete(ConfigurableListableBeanFactory beanFactory, Set<String> dataSourceInitializerNames) {
        configureOtherInitializersToDependOnJpaInitializers(beanFactory, dataSourceInitializerNames);
    }

    private void configureOtherInitializersToDependOnJpaInitializers(ConfigurableListableBeanFactory beanFactory, Set<String> dataSourceInitializerNames) {
        Set<String> jpaInitializers = new HashSet<>();
        Set<String> otherInitializers = new HashSet<>(dataSourceInitializerNames);
        Iterator<String> iterator = otherInitializers.iterator();
        while (iterator.hasNext()) {
            String initializerName = iterator.next();
            BeanDefinition initializerDefinition = beanFactory.getBeanDefinition(initializerName);
            if (JpaDatabaseInitializerDetector.class.getName().equals(initializerDefinition.getAttribute(DatabaseInitializerDetector.class.getName()))) {
                iterator.remove();
                jpaInitializers.add(initializerName);
            }
        }
        for (String otherInitializerName : otherInitializers) {
            BeanDefinition definition = beanFactory.getBeanDefinition(otherInitializerName);
            String[] dependencies = definition.getDependsOn();
            for (String dependencyName : jpaInitializers) {
                dependencies = StringUtils.addStringToArray(dependencies, dependencyName);
            }
            definition.setDependsOn(dependencies);
        }
    }
}
