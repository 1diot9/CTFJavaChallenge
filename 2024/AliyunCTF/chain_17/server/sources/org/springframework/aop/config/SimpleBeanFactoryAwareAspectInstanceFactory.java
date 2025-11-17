package org.springframework.aop.config;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/SimpleBeanFactoryAwareAspectInstanceFactory.class */
public class SimpleBeanFactoryAwareAspectInstanceFactory implements AspectInstanceFactory, BeanFactoryAware {

    @Nullable
    private String aspectBeanName;

    @Nullable
    private BeanFactory beanFactory;

    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Assert.notNull(this.aspectBeanName, "'aspectBeanName' is required");
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public Object getAspectInstance() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Assert.state(this.aspectBeanName != null, "No 'aspectBeanName' set");
        return this.beanFactory.getBean(this.aspectBeanName);
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            return cbf.getBeanClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.beanFactory != null && this.aspectBeanName != null && this.beanFactory.isSingleton(this.aspectBeanName) && this.beanFactory.isTypeMatch(this.aspectBeanName, Ordered.class)) {
            return ((Ordered) this.beanFactory.getBean(this.aspectBeanName)).getOrder();
        }
        return Integer.MAX_VALUE;
    }
}
