package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/InstantiationStrategy.class */
public interface InstantiationStrategy {
    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) throws BeansException;

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, Constructor<?> ctor, Object... args) throws BeansException;

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Object factoryBean, Method factoryMethod, Object... args) throws BeansException;

    default Class<?> getActualBeanClass(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        return bd.getBeanClass();
    }
}
