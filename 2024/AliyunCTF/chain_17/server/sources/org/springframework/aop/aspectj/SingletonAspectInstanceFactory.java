package org.springframework.aop.aspectj;

import java.io.Serializable;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/SingletonAspectInstanceFactory.class */
public class SingletonAspectInstanceFactory implements AspectInstanceFactory, Serializable {
    private final Object aspectInstance;

    public SingletonAspectInstanceFactory(Object aspectInstance) {
        Assert.notNull(aspectInstance, "Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public final Object getAspectInstance() {
        return this.aspectInstance;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.aspectInstance.getClass().getClassLoader();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        Object obj = this.aspectInstance;
        if (obj instanceof Ordered) {
            Ordered ordered = (Ordered) obj;
            return ordered.getOrder();
        }
        return getOrderForAspectClass(this.aspectInstance.getClass());
    }

    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}
