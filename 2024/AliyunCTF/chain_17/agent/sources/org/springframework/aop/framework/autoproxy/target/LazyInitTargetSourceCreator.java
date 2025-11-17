package org.springframework.aop.framework.autoproxy.target;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/target/LazyInitTargetSourceCreator.class */
public class LazyInitTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator {
    @Override // org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator
    protected boolean isPrototypeBased() {
        return false;
    }

    @Override // org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator
    @Nullable
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
        BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            BeanDefinition definition = clbf.getBeanDefinition(beanName);
            if (definition.isLazyInit()) {
                return new LazyInitTargetSource();
            }
            return null;
        }
        return null;
    }
}
