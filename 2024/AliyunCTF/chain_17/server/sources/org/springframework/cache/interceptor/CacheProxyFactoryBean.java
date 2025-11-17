package org.springframework.cache.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.CacheManager;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheProxyFactoryBean.class */
public class CacheProxyFactoryBean extends AbstractSingletonProxyFactoryBean implements BeanFactoryAware, SmartInitializingSingleton {
    private final CacheInterceptor cacheInterceptor = new CacheInterceptor();
    private Pointcut pointcut = Pointcut.TRUE;

    public void setCacheOperationSources(CacheOperationSource... cacheOperationSources) {
        this.cacheInterceptor.setCacheOperationSources(cacheOperationSources);
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.cacheInterceptor.setKeyGenerator(keyGenerator);
    }

    public void setCacheResolver(CacheResolver cacheResolver) {
        this.cacheInterceptor.setCacheResolver(cacheResolver);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheInterceptor.setCacheManager(cacheManager);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.cacheInterceptor.setBeanFactory(beanFactory);
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        this.cacheInterceptor.afterSingletonsInstantiated();
    }

    @Override // org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
    protected Object createMainInterceptor() {
        this.cacheInterceptor.afterPropertiesSet();
        return new DefaultPointcutAdvisor(this.pointcut, this.cacheInterceptor);
    }
}
