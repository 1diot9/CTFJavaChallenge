package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/AspectComponentDefinition.class */
public class AspectComponentDefinition extends CompositeComponentDefinition {
    private final BeanDefinition[] beanDefinitions;
    private final BeanReference[] beanReferences;

    public AspectComponentDefinition(String aspectName, @Nullable BeanDefinition[] beanDefinitions, @Nullable BeanReference[] beanReferences, @Nullable Object source) {
        super(aspectName, source);
        this.beanDefinitions = beanDefinitions != null ? beanDefinitions : new BeanDefinition[0];
        this.beanReferences = beanReferences != null ? beanReferences : new BeanReference[0];
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
}
