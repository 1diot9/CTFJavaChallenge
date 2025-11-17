package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/DefaultPointcutAdvisor.class */
public class DefaultPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable {
    private Pointcut pointcut;

    public DefaultPointcutAdvisor() {
        this.pointcut = Pointcut.TRUE;
    }

    public DefaultPointcutAdvisor(Advice advice) {
        this(Pointcut.TRUE, advice);
    }

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = Pointcut.TRUE;
        this.pointcut = pointcut;
        setAdvice(advice);
    }

    public void setPointcut(@Nullable Pointcut pointcut) {
        this.pointcut = pointcut != null ? pointcut : Pointcut.TRUE;
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override // org.springframework.aop.support.AbstractGenericPointcutAdvisor
    public String toString() {
        return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
    }
}
