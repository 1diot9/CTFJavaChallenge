package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/DestructionAwareBeanPostProcessor.class */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

    default boolean requiresDestruction(Object bean) {
        return true;
    }
}
