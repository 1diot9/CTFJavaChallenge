package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/InfrastructureAdvisorAutoProxyCreator.class */
public class InfrastructureAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator {

    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
    public void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.initBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
    protected boolean isEligibleAdvisorBean(String beanName) {
        return this.beanFactory != null && this.beanFactory.containsBeanDefinition(beanName) && this.beanFactory.getBeanDefinition(beanName).getRole() == 2;
    }
}
