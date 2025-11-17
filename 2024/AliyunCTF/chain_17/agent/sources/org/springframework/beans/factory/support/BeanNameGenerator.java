package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanNameGenerator.class */
public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);
}
