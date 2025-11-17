package org.springframework.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/FactoryBeanRegistrySupport.class */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap(16);

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        try {
            return factoryBean.getObjectType();
        } catch (Throwable ex) {
            this.logger.info("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolvableType getTypeForFactoryBeanFromAttributes(AttributeAccessor attributes) {
        Object attribute = attributes.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
        if (attribute == null) {
            return ResolvableType.NONE;
        }
        if (attribute instanceof ResolvableType) {
            ResolvableType resolvableType = (ResolvableType) attribute;
            return resolvableType;
        }
        if (attribute instanceof Class) {
            Class<?> clazz = (Class) attribute;
            return ResolvableType.forClass(clazz);
        }
        throw new IllegalArgumentException("Invalid value type for attribute 'factoryBeanObjectType': " + attribute.getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolvableType getFactoryBeanGeneric(@Nullable ResolvableType type) {
        return type != null ? type.as(FactoryBean.class).getGeneric(new int[0]) : ResolvableType.NONE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && containsSingleton(beanName)) {
            synchronized (getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = doGetObjectFromFactoryBean(factory, beanName);
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    } else {
                        if (shouldPostProcess) {
                            if (isSingletonCurrentlyInCreation(beanName)) {
                                return object;
                            }
                            beforeSingletonCreation(beanName);
                            try {
                                try {
                                    object = postProcessObjectFromFactoryBean(object, beanName);
                                    afterSingletonCreation(beanName);
                                } catch (Throwable ex) {
                                    throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", ex);
                                }
                            } catch (Throwable th) {
                                afterSingletonCreation(beanName);
                                throw th;
                            }
                        }
                        if (containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }
                return object;
            }
        }
        Object object2 = doGetObjectFromFactoryBean(factory, beanName);
        if (shouldPostProcess) {
            try {
                object2 = postProcessObjectFromFactoryBean(object2, beanName);
            } catch (Throwable ex2) {
                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex2);
            }
        }
        return object2;
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
        try {
            Object object = factory.getObject();
            if (object == null) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
                }
                object = new NullBean();
            }
            return object;
        } catch (FactoryBeanNotInitializedException ex) {
            throw new BeanCurrentlyInCreationException(beanName, ex.toString());
        } catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex2);
        }
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        FactoryBean<?> factoryBean = (FactoryBean) beanInstance;
        return factoryBean;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void removeSingleton(String beanName) {
        synchronized (getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanObjectCache.remove(beanName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void clearSingletonCache() {
        synchronized (getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanObjectCache.clear();
        }
    }
}
