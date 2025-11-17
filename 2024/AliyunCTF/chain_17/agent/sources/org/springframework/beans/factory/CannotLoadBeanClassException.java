package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/CannotLoadBeanClassException.class */
public class CannotLoadBeanClassException extends FatalBeanException {

    @Nullable
    private final String resourceDescription;
    private final String beanName;

    @Nullable
    private final String beanClassName;

    public CannotLoadBeanClassException(@Nullable String resourceDescription, String beanName, @Nullable String beanClassName, ClassNotFoundException cause) {
        super("Cannot find class [" + beanClassName + "] for bean with name '" + beanName + "'" + (resourceDescription != null ? " defined in " + resourceDescription : ""), cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }

    public CannotLoadBeanClassException(@Nullable String resourceDescription, String beanName, @Nullable String beanClassName, LinkageError cause) {
        super("Error loading class [" + beanClassName + "] for bean with name '" + beanName + "'" + (resourceDescription != null ? " defined in " + resourceDescription : "") + ": problem with class file or dependent class", cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }

    @Nullable
    public String getResourceDescription() {
        return this.resourceDescription;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Nullable
    public String getBeanClassName() {
        return this.beanClassName;
    }
}
