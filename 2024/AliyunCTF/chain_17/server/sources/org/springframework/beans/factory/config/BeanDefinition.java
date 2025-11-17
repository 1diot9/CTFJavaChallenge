package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanDefinition.class */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";
    public static final int ROLE_APPLICATION = 0;
    public static final int ROLE_SUPPORT = 1;
    public static final int ROLE_INFRASTRUCTURE = 2;

    void setParentName(@Nullable String parentName);

    @Nullable
    String getParentName();

    void setBeanClassName(@Nullable String beanClassName);

    @Nullable
    String getBeanClassName();

    void setScope(@Nullable String scope);

    @Nullable
    String getScope();

    void setLazyInit(boolean lazyInit);

    boolean isLazyInit();

    void setDependsOn(@Nullable String... dependsOn);

    @Nullable
    String[] getDependsOn();

    void setAutowireCandidate(boolean autowireCandidate);

    boolean isAutowireCandidate();

    void setPrimary(boolean primary);

    boolean isPrimary();

    void setFactoryBeanName(@Nullable String factoryBeanName);

    @Nullable
    String getFactoryBeanName();

    void setFactoryMethodName(@Nullable String factoryMethodName);

    @Nullable
    String getFactoryMethodName();

    ConstructorArgumentValues getConstructorArgumentValues();

    MutablePropertyValues getPropertyValues();

    void setInitMethodName(@Nullable String initMethodName);

    @Nullable
    String getInitMethodName();

    void setDestroyMethodName(@Nullable String destroyMethodName);

    @Nullable
    String getDestroyMethodName();

    void setRole(int role);

    int getRole();

    void setDescription(@Nullable String description);

    @Nullable
    String getDescription();

    ResolvableType getResolvableType();

    boolean isSingleton();

    boolean isPrototype();

    boolean isAbstract();

    @Nullable
    String getResourceDescription();

    @Nullable
    BeanDefinition getOriginatingBeanDefinition();

    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }
}
