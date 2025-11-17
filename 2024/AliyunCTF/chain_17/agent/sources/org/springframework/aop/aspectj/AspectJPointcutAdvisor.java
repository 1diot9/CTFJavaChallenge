package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJPointcutAdvisor.class */
public class AspectJPointcutAdvisor implements PointcutAdvisor, Ordered {
    private final AbstractAspectJAdvice advice;
    private final Pointcut pointcut;

    @Nullable
    private Integer order;

    public AspectJPointcutAdvisor(AbstractAspectJAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.pointcut = advice.buildSafePointcut();
    }

    public void setOrder(int order) {
        this.order = Integer.valueOf(order);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.order != null) {
            return this.order.intValue();
        }
        return this.advice.getOrder();
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public String getAspectName() {
        return this.advice.getAspectName();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AspectJPointcutAdvisor) {
                AspectJPointcutAdvisor that = (AspectJPointcutAdvisor) other;
                if (this.advice.equals(that.advice)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (AspectJPointcutAdvisor.class.hashCode() * 29) + this.advice.hashCode();
    }
}
