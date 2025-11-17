package org.springframework.boot.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/ErrorPageRegistrarBeanPostProcessor.class */
public class ErrorPageRegistrarBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private ListableBeanFactory beanFactory;
    private List<ErrorPageRegistrar> registrars;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf((Class<?>) ListableBeanFactory.class, beanFactory, "ErrorPageRegistrarBeanPostProcessor can only be used with a ListableBeanFactory");
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ErrorPageRegistry) {
            ErrorPageRegistry errorPageRegistry = (ErrorPageRegistry) bean;
            postProcessBeforeInitialization(errorPageRegistry);
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void postProcessBeforeInitialization(ErrorPageRegistry registry) {
        for (ErrorPageRegistrar registrar : getRegistrars()) {
            registrar.registerErrorPages(registry);
        }
    }

    private Collection<ErrorPageRegistrar> getRegistrars() {
        if (this.registrars == null) {
            this.registrars = new ArrayList(this.beanFactory.getBeansOfType(ErrorPageRegistrar.class, false, false).values());
            this.registrars.sort(AnnotationAwareOrderComparator.INSTANCE);
            this.registrars = Collections.unmodifiableList(this.registrars);
        }
        return this.registrars;
    }
}
