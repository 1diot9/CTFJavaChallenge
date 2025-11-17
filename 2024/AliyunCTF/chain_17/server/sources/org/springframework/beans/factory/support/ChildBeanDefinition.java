package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ChildBeanDefinition.class */
public class ChildBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private String parentName;

    public ChildBeanDefinition(String parentName) {
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, MutablePropertyValues pvs) {
        super(null, pvs);
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        setBeanClass(beanClass);
    }

    public ChildBeanDefinition(String parentName, String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        setBeanClassName(beanClassName);
    }

    public ChildBeanDefinition(ChildBeanDefinition original) {
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
    public void validate() throws BeanDefinitionValidationException {
        super.validate();
        if (this.parentName == null) {
            throw new BeanDefinitionValidationException("'parentName' must be set in ChildBeanDefinition");
        }
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new ChildBeanDefinition(this);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ChildBeanDefinition) {
                ChildBeanDefinition that = (ChildBeanDefinition) other;
                if (!ObjectUtils.nullSafeEquals(this.parentName, that.parentName) || !super.equals(other)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.core.AttributeAccessorSupport
    public int hashCode() {
        return (ObjectUtils.nullSafeHashCode(this.parentName) * 29) + super.hashCode();
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public String toString() {
        return "Child bean with parent '" + this.parentName + "': " + super.toString();
    }
}
