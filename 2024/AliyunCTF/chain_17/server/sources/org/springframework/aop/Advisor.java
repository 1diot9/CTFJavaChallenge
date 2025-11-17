package org.springframework.aop;

import org.aopalliance.aop.Advice;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/Advisor.class */
public interface Advisor {
    public static final Advice EMPTY_ADVICE = new Advice() { // from class: org.springframework.aop.Advisor.1
    };

    Advice getAdvice();

    default boolean isPerInstance() {
        return true;
    }
}
