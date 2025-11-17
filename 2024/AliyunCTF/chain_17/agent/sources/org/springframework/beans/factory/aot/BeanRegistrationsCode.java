package org.springframework.beans.factory.aot;

import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.javapoet.ClassName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationsCode.class */
public interface BeanRegistrationsCode {
    ClassName getClassName();

    GeneratedMethods getMethods();
}
