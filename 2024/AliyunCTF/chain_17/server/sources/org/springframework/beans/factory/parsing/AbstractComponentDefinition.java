package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/AbstractComponentDefinition.class */
public abstract class AbstractComponentDefinition implements ComponentDefinition {
    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getDescription() {
        return getName();
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[0];
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getInnerBeanDefinitions() {
        return new BeanDefinition[0];
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanReference[] getBeanReferences() {
        return new BeanReference[0];
    }

    public String toString() {
        return getDescription();
    }
}
