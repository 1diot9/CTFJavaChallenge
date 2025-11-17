package org.springframework.util.concurrent;

@FunctionalInterface
@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/FailureCallback.class */
public interface FailureCallback {
    void onFailure(Throwable ex);
}
