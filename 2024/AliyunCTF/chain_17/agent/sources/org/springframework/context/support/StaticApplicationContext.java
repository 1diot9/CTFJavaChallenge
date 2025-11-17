package org.springframework.context.support;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/StaticApplicationContext.class */
public class StaticApplicationContext extends GenericApplicationContext {
    private final StaticMessageSource staticMessageSource;

    public StaticApplicationContext() throws BeansException {
        this(null);
    }

    public StaticApplicationContext(@Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        this.staticMessageSource = new StaticMessageSource();
        getBeanFactory().registerSingleton(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, this.staticMessageSource);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void assertBeanFactoryActive() {
    }

    public final StaticMessageSource getStaticMessageSource() {
        return this.staticMessageSource;
    }

    public void registerSingleton(String name, Class<?> clazz) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }

    public void registerSingleton(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }

    public void registerPrototype(String name, Class<?> clazz) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }

    public void registerPrototype(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }

    public void addMessage(String code, Locale locale, String defaultMessage) {
        getStaticMessageSource().addMessage(code, locale, defaultMessage);
    }
}
