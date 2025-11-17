package org.springframework.beans.factory.aot;

import org.springframework.aot.generate.GenerationContext;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanFactoryInitializationAotContribution.class */
public interface BeanFactoryInitializationAotContribution {
    void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode);
}
