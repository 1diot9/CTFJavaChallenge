package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/AbstractBeanFactoryAwareAdvisingPostProcessor.class */
public abstract class AbstractBeanFactoryAwareAdvisingPostProcessor extends AbstractAdvisingBeanPostProcessor implements BeanFactoryAware {

    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        ConfigurableListableBeanFactory configurableListableBeanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            configurableListableBeanFactory = clbf;
        } else {
            configurableListableBeanFactory = null;
        }
        this.beanFactory = configurableListableBeanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor
    public ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        if (this.beanFactory != null) {
            AutoProxyUtils.exposeTargetClass(this.beanFactory, beanName, bean.getClass());
        }
        ProxyFactory proxyFactory = super.prepareProxyFactory(bean, beanName);
        if (!proxyFactory.isProxyTargetClass() && this.beanFactory != null && AutoProxyUtils.shouldProxyTargetClass(this.beanFactory, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        }
        return proxyFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor
    public boolean isEligible(Object bean, String beanName) {
        return !AutoProxyUtils.isOriginalInstance(beanName, bean.getClass()) && super.isEligible(bean, beanName);
    }
}
