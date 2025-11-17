package org.springframework.beans.factory.aot;

import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.MethodReference;
import org.springframework.javapoet.ClassName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationCode.class */
public interface BeanRegistrationCode {
    ClassName getClassName();

    GeneratedMethods getMethods();

    void addInstancePostProcessor(MethodReference methodReference);
}
