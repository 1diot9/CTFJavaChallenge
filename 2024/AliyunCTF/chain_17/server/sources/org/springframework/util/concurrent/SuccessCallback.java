package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/SuccessCallback.class */
public interface SuccessCallback<T> {
    void onSuccess(@Nullable T result);
}
