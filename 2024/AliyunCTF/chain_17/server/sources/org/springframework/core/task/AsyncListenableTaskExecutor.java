package org.springframework.core.task;

import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/AsyncListenableTaskExecutor.class */
public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {
    @Deprecated
    ListenableFuture<?> submitListenable(Runnable task);

    @Deprecated
    <T> ListenableFuture<T> submitListenable(Callable<T> task);
}
