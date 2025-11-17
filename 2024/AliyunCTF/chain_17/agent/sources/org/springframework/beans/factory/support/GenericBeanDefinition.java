package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/GenericBeanDefinition.class */
public class GenericBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private String parentName;

    public GenericBeanDefinition() {
    }

    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setParentName(@Nullable String parentName) {
        this.parentName = parentName;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getParentName() {
        return this.parentName;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof GenericBeanDefinition) {
                GenericBeanDefinition that = (GenericBeanDefinition) other;
                if (!ObjectUtils.nullSafeEquals(this.parentName, that.parentName) || !super.equals(other)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public String toString() {
        if (this.parentName != null) {
            return "Generic bean with parent '" + this.parentName + "': " + super.toString();
        }
        return "Generic bean: " + super.toString();
    }
}
