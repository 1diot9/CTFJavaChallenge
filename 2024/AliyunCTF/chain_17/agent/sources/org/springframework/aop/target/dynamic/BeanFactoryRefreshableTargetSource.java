package org.springframework.aop.target.dynamic;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/dynamic/BeanFactoryRefreshableTargetSource.class */
public class BeanFactoryRefreshableTargetSource extends AbstractRefreshableTargetSource {
    private final BeanFactory beanFactory;
    private final String beanName;

    public BeanFactoryRefreshableTargetSource(BeanFactory beanFactory, String beanName) {
        Assert.notNull(beanFactory, "BeanFactory is required");
        Assert.notNull(beanName, "Bean name is required");
        this.beanFactory = beanFactory;
        this.beanName = beanName;
    }

    @Override // org.springframework.aop.target.dynamic.AbstractRefreshableTargetSource
    protected final Object freshTarget() {
        return obtainFreshBean(this.beanFactory, this.beanName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object obtainFreshBean(BeanFactory beanFactory, String beanName) {
        return beanFactory.getBean(beanName);
    }
}
