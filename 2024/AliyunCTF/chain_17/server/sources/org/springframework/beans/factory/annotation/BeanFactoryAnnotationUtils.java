package org.springframework.beans.factory.annotation;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/BeanFactoryAnnotationUtils.class */
public abstract class BeanFactoryAnnotationUtils {
    public static <T> Map<String, T> qualifiedBeansOfType(ListableBeanFactory beanFactory, Class<T> beanType, String qualifier) throws BeansException {
        String[] candidateBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, (Class<?>) beanType);
        LinkedHashMap linkedHashMap = new LinkedHashMap(4);
        for (String beanName : candidateBeans) {
            Objects.requireNonNull(qualifier);
            if (isQualifierMatch((v1) -> {
                return r0.equals(v1);
            }, beanName, beanFactory)) {
                linkedHashMap.put(beanName, beanFactory.getBean(beanName, beanType));
            }
        }
        return linkedHashMap;
    }

    public static <T> T qualifiedBeanOfType(BeanFactory beanFactory, Class<T> cls, String str) throws BeansException {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        if (beanFactory instanceof ListableBeanFactory) {
            return (T) qualifiedBeanOfType((ListableBeanFactory) beanFactory, (Class) cls, str);
        }
        if (beanFactory.containsBean(str)) {
            return (T) beanFactory.getBean(str, cls);
        }
        throw new NoSuchBeanDefinitionException(str, "No matching " + cls.getSimpleName() + " bean found for bean name '" + str + "'! (Note: Qualifier matching not supported because given BeanFactory does not implement ConfigurableListableBeanFactory.)");
    }

    private static <T> T qualifiedBeanOfType(ListableBeanFactory listableBeanFactory, Class<T> cls, String str) {
        String str2 = null;
        for (String str3 : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(listableBeanFactory, (Class<?>) cls)) {
            Objects.requireNonNull(str);
            if (isQualifierMatch((v1) -> {
                return r0.equals(v1);
            }, str3, listableBeanFactory)) {
                if (str2 != null) {
                    throw new NoUniqueBeanDefinitionException((Class<?>) cls, str2, str3);
                }
                str2 = str3;
            }
        }
        if (str2 != null) {
            return (T) listableBeanFactory.getBean(str2, cls);
        }
        if (listableBeanFactory.containsBean(str)) {
            return (T) listableBeanFactory.getBean(str, cls);
        }
        throw new NoSuchBeanDefinitionException(str, "No matching " + cls.getSimpleName() + " bean found for qualifier '" + str + "' - neither qualifier match nor bean name match!");
    }

    public static boolean isQualifierMatch(Predicate<String> qualifier, String beanName, @Nullable BeanFactory beanFactory) {
        Qualifier targetAnnotation;
        Qualifier targetAnnotation2;
        Object value;
        if (qualifier.test(beanName)) {
            return true;
        }
        if (beanFactory != null) {
            for (String alias : beanFactory.getAliases(beanName)) {
                if (qualifier.test(alias)) {
                    return true;
                }
            }
            try {
                Class<?> beanType = beanFactory.getType(beanName);
                if (beanFactory instanceof ConfigurableBeanFactory) {
                    ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
                    BeanDefinition bd = cbf.getMergedBeanDefinition(beanName);
                    if (bd instanceof AbstractBeanDefinition) {
                        AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
                        AutowireCandidateQualifier candidate = abd.getQualifier(Qualifier.class.getName());
                        if (candidate != null && (value = candidate.getAttribute("value")) != null && qualifier.test(value.toString())) {
                            return true;
                        }
                    }
                    if (bd instanceof RootBeanDefinition) {
                        RootBeanDefinition rbd = (RootBeanDefinition) bd;
                        Method factoryMethod = rbd.getResolvedFactoryMethod();
                        if (factoryMethod != null && (targetAnnotation2 = (Qualifier) AnnotationUtils.getAnnotation(factoryMethod, Qualifier.class)) != null) {
                            return qualifier.test(targetAnnotation2.value());
                        }
                    }
                }
                if (beanType != null && (targetAnnotation = (Qualifier) AnnotationUtils.getAnnotation(beanType, Qualifier.class)) != null) {
                    return qualifier.test(targetAnnotation.value());
                }
                return false;
            } catch (NoSuchBeanDefinitionException e) {
                return false;
            }
        }
        return false;
    }
}
