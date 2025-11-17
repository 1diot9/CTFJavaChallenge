package org.springframework.beans.factory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanIsNotAFactoryException.class */
public class BeanIsNotAFactoryException extends BeanNotOfRequiredTypeException {
    public BeanIsNotAFactoryException(String name, Class<?> actualType) {
        super(name, FactoryBean.class, actualType);
    }
}
