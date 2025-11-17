package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/MergedBeanDefinitionPostProcessor.class */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
    void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

    default void resetBeanDefinition(String beanName) {
    }
}
