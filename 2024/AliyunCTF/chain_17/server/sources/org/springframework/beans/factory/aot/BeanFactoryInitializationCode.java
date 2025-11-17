package org.springframework.beans.factory.aot;

import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.MethodReference;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanFactoryInitializationCode.class */
public interface BeanFactoryInitializationCode {
    public static final String BEAN_FACTORY_VARIABLE = "beanFactory";

    GeneratedMethods getMethods();

    void addInitializer(MethodReference methodReference);
}
