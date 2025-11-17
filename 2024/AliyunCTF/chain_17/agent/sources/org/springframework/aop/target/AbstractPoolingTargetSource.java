package org.springframework.aop.target;

import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/AbstractPoolingTargetSource.class */
public abstract class AbstractPoolingTargetSource extends AbstractPrototypeBasedTargetSource implements PoolingConfig, DisposableBean {
    private int maxSize = -1;

    protected abstract void createPool() throws Exception;

    @Override // org.springframework.aop.TargetSource
    @Nullable
    public abstract Object getTarget() throws Exception;

    @Override // org.springframework.aop.TargetSource
    public abstract void releaseTarget(Object target) throws Exception;

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override // org.springframework.aop.target.PoolingConfig
    public int getMaxSize() {
        return this.maxSize;
    }

    @Override // org.springframework.aop.target.AbstractPrototypeBasedTargetSource, org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource, org.springframework.beans.factory.BeanFactoryAware
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        try {
            createPool();
        } catch (Throwable ex) {
            throw new BeanInitializationException("Could not create instance pool for TargetSource", ex);
        }
    }

    public DefaultIntroductionAdvisor getPoolingConfigMixin() {
        DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, (Class<?>) PoolingConfig.class);
    }
}
