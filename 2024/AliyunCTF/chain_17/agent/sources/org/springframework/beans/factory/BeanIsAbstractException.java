package org.springframework.beans.factory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanIsAbstractException.class */
public class BeanIsAbstractException extends BeanCreationException {
    public BeanIsAbstractException(String beanName) {
        super(beanName, "Bean definition is abstract");
    }
}
