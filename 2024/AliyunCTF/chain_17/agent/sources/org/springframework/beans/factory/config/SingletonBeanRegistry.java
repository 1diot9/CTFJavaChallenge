package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/SingletonBeanRegistry.class */
public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    @Nullable
    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}
