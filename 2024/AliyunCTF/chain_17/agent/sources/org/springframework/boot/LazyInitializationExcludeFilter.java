package org.springframework.boot;

import org.springframework.beans.factory.config.BeanDefinition;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/LazyInitializationExcludeFilter.class */
public interface LazyInitializationExcludeFilter {
    boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType);

    static LazyInitializationExcludeFilter forBeanTypes(Class<?>... types) {
        return (beanName, beanDefinition, beanType) -> {
            for (Class cls : types) {
                if (cls.isAssignableFrom(beanType)) {
                    return true;
                }
            }
            return false;
        };
    }
}
