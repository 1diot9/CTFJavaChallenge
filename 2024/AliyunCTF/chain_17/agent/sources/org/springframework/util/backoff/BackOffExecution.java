package org.springframework.util.backoff;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/backoff/BackOffExecution.class */
public interface BackOffExecution {
    public static final long STOP = -1;

    long nextBackOff();
}
