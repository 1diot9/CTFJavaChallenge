package org.springframework.beans;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/NullValueInNestedPathException.class */
public class NullValueInNestedPathException extends InvalidPropertyException {
    public NullValueInNestedPathException(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName, "Value of nested property '" + propertyName + "' is null");
    }

    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg) {
        super(beanClass, propertyName, msg);
    }

    public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
        super(beanClass, propertyName, msg, cause);
    }
}
