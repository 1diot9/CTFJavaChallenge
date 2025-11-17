package org.springframework.beans.factory.config;

import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/AutowireCapableBeanFactory.class */
public interface AutowireCapableBeanFactory extends BeanFactory {
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;

    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    <T> T createBean(Class<T> beanClass) throws BeansException;

    void autowireBean(Object existingBean) throws BeansException;

    Object configureBean(Object existingBean, String beanName) throws BeansException;

    @Deprecated(since = "6.1")
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException;

    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    @Deprecated(since = "6.1")
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    @Deprecated(since = "6.1")
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;

    void destroyBean(Object existingBean);

    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;
}
