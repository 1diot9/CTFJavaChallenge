package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/RuntimeBeanNameReference.class */
public class RuntimeBeanNameReference implements BeanReference {
    private final String beanName;

    @Nullable
    private Object source;

    public RuntimeBeanNameReference(String beanName) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.config.BeanReference
    public String getBeanName() {
        return this.beanName;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof RuntimeBeanNameReference) {
                RuntimeBeanNameReference that = (RuntimeBeanNameReference) other;
                if (this.beanName.equals(that.beanName)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.beanName.hashCode();
    }

    public String toString() {
        return "<" + getBeanName() + ">";
    }
}
