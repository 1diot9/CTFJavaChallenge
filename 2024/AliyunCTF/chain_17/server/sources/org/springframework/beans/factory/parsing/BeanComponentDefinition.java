package org.springframework.beans.factory.parsing;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/BeanComponentDefinition.class */
public class BeanComponentDefinition extends BeanDefinitionHolder implements ComponentDefinition {
    private final BeanDefinition[] innerBeanDefinitions;
    private final BeanReference[] beanReferences;

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName) {
        this(new BeanDefinitionHolder(beanDefinition, beanName));
    }

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        this(new BeanDefinitionHolder(beanDefinition, beanName, aliases));
    }

    public BeanComponentDefinition(BeanDefinitionHolder beanDefinitionHolder) {
        super(beanDefinitionHolder);
        List<BeanDefinition> innerBeans = new ArrayList<>();
        List<BeanReference> references = new ArrayList<>();
        PropertyValues propertyValues = beanDefinitionHolder.getBeanDefinition().getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder beanDefHolder = (BeanDefinitionHolder) value;
                innerBeans.add(beanDefHolder.getBeanDefinition());
            } else if (value instanceof BeanDefinition) {
                BeanDefinition beanDef = (BeanDefinition) value;
                innerBeans.add(beanDef);
            } else if (value instanceof BeanReference) {
                BeanReference beanRef = (BeanReference) value;
                references.add(beanRef);
            }
        }
        this.innerBeanDefinitions = (BeanDefinition[]) innerBeans.toArray(new BeanDefinition[0]);
        this.beanReferences = (BeanReference[]) references.toArray(new BeanReference[0]);
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getName() {
        return getBeanName();
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getDescription() {
        return getShortDescription();
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{getBeanDefinition()};
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getInnerBeanDefinitions() {
        return this.innerBeanDefinitions;
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionHolder
    public String toString() {
        return getDescription();
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionHolder
    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof BeanComponentDefinition) && super.equals(other));
    }
}
