package org.springframework.util.backoff;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/backoff/BackOff.class */
public interface BackOff {
    BackOffExecution start();
}
