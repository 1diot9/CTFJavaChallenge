package org.springframework.util;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ErrorHandler.class */
public interface ErrorHandler {
    void handleError(Throwable t);
}
