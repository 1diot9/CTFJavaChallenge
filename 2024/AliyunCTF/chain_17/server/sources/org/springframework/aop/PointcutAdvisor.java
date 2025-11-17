package org.springframework.aop;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/PointcutAdvisor.class */
public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
