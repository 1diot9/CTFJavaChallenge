package org.springframework.boot;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringBootExceptionReporter.class */
public interface SpringBootExceptionReporter {
    boolean reportException(Throwable failure);
}
