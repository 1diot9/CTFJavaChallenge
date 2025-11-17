package org.springframework.cache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/BeanFactoryCacheOperationSourceAdvisor.class */
public class BeanFactoryCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    private final CacheOperationSourcePointcut pointcut = new CacheOperationSourcePointcut();

    public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
        this.pointcut.setCacheOperationSource(cacheOperationSource);
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
