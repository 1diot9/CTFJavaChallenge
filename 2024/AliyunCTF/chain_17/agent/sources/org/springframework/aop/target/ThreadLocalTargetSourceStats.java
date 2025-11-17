package org.springframework.aop.target;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/ThreadLocalTargetSourceStats.class */
public interface ThreadLocalTargetSourceStats {
    int getInvocationCount();

    int getHitCount();

    int getObjectCount();
}
