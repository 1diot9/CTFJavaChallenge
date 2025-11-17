package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/PointcutComponentDefinition.class */
public class PointcutComponentDefinition extends AbstractComponentDefinition {
    private final String pointcutBeanName;
    private final BeanDefinition pointcutDefinition;
    private final String description;

    public PointcutComponentDefinition(String pointcutBeanName, BeanDefinition pointcutDefinition, String expression) {
        Assert.notNull(pointcutBeanName, "Bean name must not be null");
        Assert.notNull(pointcutDefinition, "Pointcut definition must not be null");
        Assert.notNull(expression, "Expression must not be null");
        this.pointcutBeanName = pointcutBeanName;
        this.pointcutDefinition = pointcutDefinition;
        this.description = "Pointcut <name='" + pointcutBeanName + "', expression=[" + expression + "]>";
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getName() {
        return this.pointcutBeanName;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public String getDescription() {
        return this.description;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{this.pointcutDefinition};
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.pointcutDefinition.getSource();
    }
}
