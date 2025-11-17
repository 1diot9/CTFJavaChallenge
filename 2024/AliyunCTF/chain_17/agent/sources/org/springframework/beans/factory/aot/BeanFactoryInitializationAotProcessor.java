package org.springframework.beans.factory.aot;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanFactoryInitializationAotProcessor.class */
public interface BeanFactoryInitializationAotProcessor {
    @Nullable
    BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory);
}
