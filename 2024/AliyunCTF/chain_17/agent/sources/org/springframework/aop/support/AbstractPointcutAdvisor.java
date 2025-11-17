package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/AbstractPointcutAdvisor.class */
public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered, Serializable {

    @Nullable
    private Integer order;

    public void setOrder(int order) {
        this.order = Integer.valueOf(order);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.order != null) {
            return this.order.intValue();
        }
        Advice advice = getAdvice();
        if (advice instanceof Ordered) {
            Ordered ordered = (Ordered) advice;
            return ordered.getOrder();
        }
        return Integer.MAX_VALUE;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof PointcutAdvisor) {
                PointcutAdvisor otherAdvisor = (PointcutAdvisor) other;
                if (!ObjectUtils.nullSafeEquals(getAdvice(), otherAdvisor.getAdvice()) || !ObjectUtils.nullSafeEquals(getPointcut(), otherAdvisor.getPointcut())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}
