package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.springframework.util.concurrent.FutureUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/AsyncTaskExecutor.class */
public interface AsyncTaskExecutor extends TaskExecutor {

    @Deprecated
    public static final long TIMEOUT_IMMEDIATE = 0;

    @Deprecated
    public static final long TIMEOUT_INDEFINITE = Long.MAX_VALUE;

    @Deprecated
    default void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    default Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<>(task, null);
        execute(future);
        return future;
    }

    default <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    default CompletableFuture<Void> submitCompletable(Runnable task) {
        return CompletableFuture.runAsync(task, this);
    }

    default <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        return FutureUtils.callAsync(task, this);
    }
}
