package org.springframework.aop.target;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/PoolingConfig.class */
public interface PoolingConfig {
    int getMaxSize();

    int getActiveCount() throws UnsupportedOperationException;

    int getIdleCount() throws UnsupportedOperationException;
}
