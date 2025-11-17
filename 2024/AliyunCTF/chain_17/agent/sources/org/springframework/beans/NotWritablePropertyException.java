package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/NotWritablePropertyException.class */
public class NotWritablePropertyException extends InvalidPropertyException {

    @Nullable
    private final String[] possibleMatches;

    public NotWritablePropertyException(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName, "Bean property '" + propertyName + "' is not writable or has an invalid setter method: Does the return type of the getter match the parameter type of the setter?");
        this.possibleMatches = null;
    }

    public NotWritablePropertyException(Class<?> beanClass, String propertyName, String msg) {
        super(beanClass, propertyName, msg);
        this.possibleMatches = null;
    }

    public NotWritablePropertyException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
        super(beanClass, propertyName, msg, cause);
        this.possibleMatches = null;
    }

    public NotWritablePropertyException(Class<?> beanClass, String propertyName, String msg, String[] possibleMatches) {
        super(beanClass, propertyName, msg);
        this.possibleMatches = possibleMatches;
    }

    @Nullable
    public String[] getPossibleMatches() {
        return this.possibleMatches;
    }
}
