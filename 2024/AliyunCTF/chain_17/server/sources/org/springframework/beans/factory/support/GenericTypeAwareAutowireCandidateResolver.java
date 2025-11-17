package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Properties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/GenericTypeAwareAutowireCandidateResolver.class */
public class GenericTypeAwareAutowireCandidateResolver extends SimpleAutowireCandidateResolver implements BeanFactoryAware, Cloneable {

    @Nullable
    private BeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (!super.isAutowireCandidate(bdHolder, descriptor)) {
            return false;
        }
        return checkGenericTypeMatch(bdHolder, descriptor);
    }

    protected boolean checkGenericTypeMatch(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        Class<?> beanType;
        Class<?> typeToBeMatched;
        RootBeanDefinition dbd;
        ResolvableType dependencyType = descriptor.getResolvableType();
        if (dependencyType.getType() instanceof Class) {
            return true;
        }
        ResolvableType targetType = null;
        boolean cacheType = false;
        RootBeanDefinition rbd = null;
        BeanDefinition beanDefinition = bdHolder.getBeanDefinition();
        if (beanDefinition instanceof RootBeanDefinition) {
            RootBeanDefinition rootBeanDef = (RootBeanDefinition) beanDefinition;
            rbd = rootBeanDef;
        }
        if (rbd != null) {
            targetType = rbd.targetType;
            if (targetType == null) {
                cacheType = true;
                targetType = getReturnTypeForFactoryMethod(rbd, descriptor);
                if (targetType == null && (dbd = getResolvedDecoratedDefinition(rbd)) != null) {
                    targetType = dbd.targetType;
                    if (targetType == null) {
                        targetType = getReturnTypeForFactoryMethod(dbd, descriptor);
                    }
                }
            } else {
                Class<?> resolvedClass = targetType.resolve();
                if (resolvedClass != null && FactoryBean.class.isAssignableFrom(resolvedClass) && (typeToBeMatched = dependencyType.resolve()) != null && !FactoryBean.class.isAssignableFrom(typeToBeMatched)) {
                    targetType = targetType.getGeneric(new int[0]);
                    if (descriptor.fallbackMatchAllowed()) {
                        targetType = ResolvableType.forClass(targetType.resolve());
                    }
                }
            }
        }
        if (targetType == null) {
            if (this.beanFactory != null && (beanType = this.beanFactory.getType(bdHolder.getBeanName())) != null) {
                targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
            }
            if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null) {
                Class<?> beanClass = rbd.getBeanClass();
                if (!FactoryBean.class.isAssignableFrom(beanClass)) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
                }
            }
        }
        if (targetType == null) {
            return true;
        }
        if (cacheType) {
            rbd.targetType = targetType;
        }
        if (descriptor.fallbackMatchAllowed() && (targetType.hasUnresolvableGenerics() || targetType.resolve() == Properties.class)) {
            return true;
        }
        return dependencyType.isAssignableFrom(targetType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public RootBeanDefinition getResolvedDecoratedDefinition(RootBeanDefinition rbd) {
        BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
        if (decDef == null) {
            return null;
        }
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            if (clbf.containsBeanDefinition(decDef.getBeanName())) {
                BeanDefinition dbd = clbf.getMergedBeanDefinition(decDef.getBeanName());
                if (dbd instanceof RootBeanDefinition) {
                    RootBeanDefinition rootBeanDef = (RootBeanDefinition) dbd;
                    return rootBeanDef;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Nullable
    protected ResolvableType getReturnTypeForFactoryMethod(RootBeanDefinition rbd, DependencyDescriptor descriptor) {
        Class<?> resolvedClass;
        Method factoryMethod;
        ResolvableType returnType = rbd.factoryMethodReturnType;
        if (returnType == null && (factoryMethod = rbd.getResolvedFactoryMethod()) != null) {
            returnType = ResolvableType.forMethodReturnType(factoryMethod);
        }
        if (returnType != null && (resolvedClass = returnType.resolve()) != null && descriptor.getDependencyType().isAssignableFrom(resolvedClass)) {
            return returnType;
        }
        return null;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public AutowireCandidateResolver cloneIfNecessary() {
        try {
            return (AutowireCandidateResolver) clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
