package org.springframework.aop.aspectj;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJExpressionPointcutAdvisor.class */
public class AspectJExpressionPointcutAdvisor extends AbstractGenericPointcutAdvisor implements BeanFactoryAware {
    private final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    public void setExpression(@Nullable String expression) {
        this.pointcut.setExpression(expression);
    }

    @Nullable
    public String getExpression() {
        return this.pointcut.getExpression();
    }

    public void setLocation(@Nullable String location) {
        this.pointcut.setLocation(location);
    }

    @Nullable
    public String getLocation() {
        return this.pointcut.getLocation();
    }

    public void setParameterNames(String... names) {
        this.pointcut.setParameterNames(names);
    }

    public void setParameterTypes(Class<?>... types) {
        this.pointcut.setParameterTypes(types);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.pointcut.setBeanFactory(beanFactory);
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
