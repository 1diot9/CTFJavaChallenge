package org.springframework.aop.aspectj;

import java.lang.reflect.InvocationTargetException;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/SimpleAspectInstanceFactory.class */
public class SimpleAspectInstanceFactory implements AspectInstanceFactory {
    private final Class<?> aspectClass;

    public SimpleAspectInstanceFactory(Class<?> aspectClass) {
        Assert.notNull(aspectClass, "Aspect class must not be null");
        this.aspectClass = aspectClass;
    }

    public final Class<?> getAspectClass() {
        return this.aspectClass;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public final Object getAspectInstance() {
        try {
            return ReflectionUtils.accessibleConstructor(this.aspectClass, new Class[0]).newInstance(new Object[0]);
        } catch (IllegalAccessException ex) {
            throw new AopConfigException("Could not access aspect constructor: " + this.aspectClass.getName(), ex);
        } catch (InstantiationException ex2) {
            throw new AopConfigException("Unable to instantiate aspect class: " + this.aspectClass.getName(), ex2);
        } catch (NoSuchMethodException ex3) {
            throw new AopConfigException("No default constructor on aspect class: " + this.aspectClass.getName(), ex3);
        } catch (InvocationTargetException ex4) {
            throw new AopConfigException("Failed to invoke aspect constructor: " + this.aspectClass.getName(), ex4.getTargetException());
        }
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.aspectClass.getClassLoader();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return getOrderForAspectClass(this.aspectClass);
    }

    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}
