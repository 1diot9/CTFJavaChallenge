package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/InvalidPropertyException.class */
public class InvalidPropertyException extends FatalBeanException {
    private final Class<?> beanClass;
    private final String propertyName;

    public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg) {
        this(beanClass, propertyName, msg, null);
    }

    public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg, @Nullable Throwable cause) {
        super("Invalid property '" + propertyName + "' of bean class [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }
}
