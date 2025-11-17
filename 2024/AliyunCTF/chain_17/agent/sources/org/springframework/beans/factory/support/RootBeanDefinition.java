package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/RootBeanDefinition.class */
public class RootBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private BeanDefinitionHolder decoratedDefinition;

    @Nullable
    private AnnotatedElement qualifiedElement;
    volatile boolean stale;
    boolean allowCaching;
    boolean isFactoryMethodUnique;

    @Nullable
    volatile ResolvableType targetType;

    @Nullable
    volatile Class<?> resolvedTargetType;

    @Nullable
    volatile Boolean isFactoryBean;

    @Nullable
    volatile ResolvableType factoryMethodReturnType;

    @Nullable
    volatile Method factoryMethodToIntrospect;

    @Nullable
    volatile String resolvedDestroyMethodName;
    final Object constructorArgumentLock;

    @Nullable
    Executable resolvedConstructorOrFactoryMethod;
    boolean constructorArgumentsResolved;

    @Nullable
    Object[] resolvedConstructorArguments;

    @Nullable
    Object[] preparedConstructorArguments;
    final Object postProcessingLock;
    boolean postProcessed;

    @Nullable
    volatile Boolean beforeInstantiationResolved;

    @Nullable
    private Set<Member> externallyManagedConfigMembers;

    @Nullable
    private Set<String> externallyManagedInitMethods;

    @Nullable
    private Set<String> externallyManagedDestroyMethods;

    public RootBeanDefinition() {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
    }

    @Deprecated(since = "6.0.11")
    public RootBeanDefinition(@Nullable ResolvableType beanType) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setTargetType(beanType);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setInstanceSupplier(instanceSupplier);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setScope(scope);
        setInstanceSupplier(instanceSupplier);
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setAutowireMode(autowireMode);
        if (dependencyCheck && getResolvedAutowireMode() != 3) {
            setDependencyCheck(1);
        }
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
    }

    public RootBeanDefinition(String beanClassName) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootBeanDefinition(BeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public String getParentName() {
        return null;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setParentName(@Nullable String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }

    @Nullable
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }

    public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
        this.qualifiedElement = qualifiedElement;
    }

    @Nullable
    public AnnotatedElement getQualifiedElement() {
        return this.qualifiedElement;
    }

    public void setTargetType(@Nullable ResolvableType targetType) {
        this.targetType = targetType;
    }

    public void setTargetType(@Nullable Class<?> targetType) {
        this.targetType = targetType != null ? ResolvableType.forClass(targetType) : null;
    }

    @Nullable
    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType.resolve();
        }
        return null;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.factory.config.BeanDefinition
    public ResolvableType getResolvableType() {
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType;
        }
        ResolvableType returnType = this.factoryMethodReturnType;
        if (returnType != null) {
            return returnType;
        }
        Method factoryMethod = this.factoryMethodToIntrospect;
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return super.getResolvableType();
    }

    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        Object attribute = getAttribute(AbstractBeanDefinition.PREFERRED_CONSTRUCTORS_ATTRIBUTE);
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof Constructor) {
            Constructor<?> constructor = (Constructor) attribute;
            return new Constructor[]{constructor};
        }
        if (attribute instanceof Constructor[]) {
            return (Constructor[]) attribute;
        }
        throw new IllegalArgumentException("Invalid value type for attribute 'preferredConstructors': " + attribute.getClass().getName());
    }

    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    public void setNonUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = false;
    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(getFactoryMethodName());
    }

    public void setResolvedFactoryMethod(@Nullable Method method) {
        this.factoryMethodToIntrospect = method;
        if (method != null) {
            setUniqueFactoryMethodName(method.getName());
        }
    }

    @Nullable
    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public void setInstanceSupplier(@Nullable Supplier<?> supplier) {
        Method method;
        super.setInstanceSupplier(supplier);
        if (supplier instanceof InstanceSupplier) {
            InstanceSupplier<?> instanceSupplier = (InstanceSupplier) supplier;
            method = instanceSupplier.getFactoryMethod();
        } else {
            method = null;
        }
        Method factoryMethod = method;
        if (factoryMethod != null) {
            setResolvedFactoryMethod(factoryMethod);
        }
    }

    public void markAsPostProcessed() {
        synchronized (this.postProcessingLock) {
            this.postProcessed = true;
        }
    }

    public void registerExternallyManagedConfigMember(Member configMember) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new LinkedHashSet(1);
            }
            this.externallyManagedConfigMembers.add(configMember);
        }
    }

    public boolean isExternallyManagedConfigMember(Member configMember) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers.contains(configMember);
        }
        return z;
    }

    public Set<Member> getExternallyManagedConfigMembers() {
        Set<Member> emptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers != null) {
                emptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedConfigMembers));
            } else {
                emptySet = Collections.emptySet();
            }
        }
        return emptySet;
    }

    public void registerExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new LinkedHashSet(1);
            }
            this.externallyManagedInitMethods.add(initMethod);
        }
    }

    public boolean isExternallyManagedInitMethod(String initMethod) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedInitMethods != null && this.externallyManagedInitMethods.contains(initMethod);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAnyExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedInitMethod(initMethod)) {
                return true;
            }
            return hasAnyExternallyManagedMethod(this.externallyManagedInitMethods, initMethod);
        }
    }

    public Set<String> getExternallyManagedInitMethods() {
        Set<String> emptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods != null) {
                emptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedInitMethods));
            } else {
                emptySet = Collections.emptySet();
            }
        }
        return emptySet;
    }

    public void resolveDestroyMethodIfNecessary() {
        setDestroyMethodNames(DisposableBeanAdapter.inferDestroyMethodsIfNecessary(getResolvableType().toClass(), this));
    }

    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new LinkedHashSet(1);
            }
            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }

    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods.contains(destroyMethod);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAnyExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedDestroyMethod(destroyMethod)) {
                return true;
            }
            return hasAnyExternallyManagedMethod(this.externallyManagedDestroyMethods, destroyMethod);
        }
    }

    private static boolean hasAnyExternallyManagedMethod(Set<String> candidates, String methodName) {
        if (candidates != null) {
            for (String candidate : candidates) {
                int indexOfDot = candidate.lastIndexOf(46);
                if (indexOfDot > 0) {
                    String candidateMethodName = candidate.substring(indexOfDot + 1);
                    if (candidateMethodName.equals(methodName)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public Set<String> getExternallyManagedDestroyMethods() {
        Set<String> emptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods != null) {
                emptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedDestroyMethods));
            } else {
                emptySet = Collections.emptySet();
            }
        }
        return emptySet;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof RootBeanDefinition) && super.equals(other));
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public String toString() {
        return "Root bean: " + super.toString();
    }
}
