package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanExpressionContext.class */
public class BeanExpressionContext {
    private final ConfigurableBeanFactory beanFactory;

    @Nullable
    private final Scope scope;

    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, @Nullable Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Nullable
    public final Scope getScope() {
        return this.scope;
    }

    public boolean containsObject(String key) {
        return this.beanFactory.containsBean(key) || !(this.scope == null || this.scope.resolveContextualObject(key) == null);
    }

    @Nullable
    public Object getObject(String key) {
        if (this.beanFactory.containsBean(key)) {
            return this.beanFactory.getBean(key);
        }
        if (this.scope != null) {
            return this.scope.resolveContextualObject(key);
        }
        return null;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BeanExpressionContext) {
                BeanExpressionContext that = (BeanExpressionContext) other;
                if (this.beanFactory != that.beanFactory || this.scope != that.scope) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.beanFactory.hashCode();
    }
}
