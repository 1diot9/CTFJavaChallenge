package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/CustomAutowireConfigurer.class */
public class CustomAutowireConfigurer implements BeanFactoryPostProcessor, BeanClassLoaderAware, Ordered {

    @Nullable
    private Set<?> customQualifierTypes;
    private int order = Integer.MAX_VALUE;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public void setCustomQualifierTypes(Set<?> customQualifierTypes) {
        this.customQualifierTypes = customQualifierTypes;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Class customType;
        if (this.customQualifierTypes != null) {
            if (!(beanFactory instanceof DefaultListableBeanFactory)) {
                throw new IllegalStateException("CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
            }
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
            if (!(dlbf.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
                dlbf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
            }
            QualifierAnnotationAutowireCandidateResolver resolver = (QualifierAnnotationAutowireCandidateResolver) dlbf.getAutowireCandidateResolver();
            for (Object value : this.customQualifierTypes) {
                if (value instanceof Class) {
                    customType = (Class) value;
                } else if (value instanceof String) {
                    String className = (String) value;
                    customType = ClassUtils.resolveClassName(className, this.beanClassLoader);
                } else {
                    throw new IllegalArgumentException("Invalid value [" + value + "] for custom qualifier type: needs to be Class or String.");
                }
                if (!Annotation.class.isAssignableFrom(customType)) {
                    throw new IllegalArgumentException("Qualifier type [" + customType.getName() + "] needs to be annotation type");
                }
                resolver.addQualifierType(customType);
            }
        }
    }
}
