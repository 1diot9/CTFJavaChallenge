package org.springframework.beans.factory.config;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanDefinitionCustomizer.class */
public interface BeanDefinitionCustomizer {
    void customize(BeanDefinition bd);
}
