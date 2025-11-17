package org.springframework.aop.aspectj;

import org.springframework.aop.PointcutAdvisor;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/InstantiationModelAwarePointcutAdvisor.class */
public interface InstantiationModelAwarePointcutAdvisor extends PointcutAdvisor {
    boolean isLazy();

    boolean isAdviceInstantiated();
}
