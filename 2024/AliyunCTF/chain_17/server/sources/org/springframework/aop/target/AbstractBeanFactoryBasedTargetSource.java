package org.springframework.aop.target;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/AbstractBeanFactoryBasedTargetSource.class */
public abstract class AbstractBeanFactoryBasedTargetSource implements TargetSource, BeanFactoryAware, Serializable {
    private static final long serialVersionUID = -4721607536018568393L;
    protected final transient Log logger = LogFactory.getLog(getClass());

    @Nullable
    private String targetBeanName;

    @Nullable
    private volatile Class<?> targetClass;

    @Nullable
    private BeanFactory beanFactory;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getTargetBeanName() {
        Assert.state(this.targetBeanName != null, "Target bean name not set");
        return this.targetBeanName;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.targetBeanName == null) {
            throw new IllegalStateException("Property 'targetBeanName' is required");
        }
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        Assert.state(this.beanFactory != null, "BeanFactory not set");
        return this.beanFactory;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        Class<?> cls;
        Class<?> targetClass = this.targetClass;
        if (targetClass != null) {
            return targetClass;
        }
        synchronized (this) {
            Class<?> targetClass2 = this.targetClass;
            if (targetClass2 == null && this.beanFactory != null && this.targetBeanName != null) {
                targetClass2 = this.beanFactory.getType(this.targetBeanName);
                if (targetClass2 == null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Getting bean with name '" + this.targetBeanName + "' for type determination");
                    }
                    Object beanInstance = this.beanFactory.getBean(this.targetBeanName);
                    targetClass2 = beanInstance.getClass();
                }
                this.targetClass = targetClass2;
            }
            cls = targetClass2;
        }
        return cls;
    }

    protected void copyFrom(AbstractBeanFactoryBasedTargetSource other) {
        this.targetBeanName = other.targetBeanName;
        this.targetClass = other.targetClass;
        this.beanFactory = other.beanFactory;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource) other;
        return ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) && ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName);
    }

    public int hashCode() {
        return Objects.hash(getClass(), this.targetBeanName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" for target bean '").append(this.targetBeanName).append('\'');
        Class<?> targetClass = this.targetClass;
        if (targetClass != null) {
            sb.append(" of type [").append(targetClass.getName()).append(']');
        }
        return sb.toString();
    }
}
