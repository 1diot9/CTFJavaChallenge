package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/FutureUtils.class */
public abstract class FutureUtils {
    public static <T> CompletableFuture<T> callAsync(Callable<T> callable) {
        Assert.notNull(callable, "Callable must not be null");
        CompletableFuture<T> result = new CompletableFuture<>();
        return result.completeAsync(toSupplier(callable, result));
    }

    public static <T> CompletableFuture<T> callAsync(Callable<T> callable, Executor executor) {
        Assert.notNull(callable, "Callable must not be null");
        Assert.notNull(executor, "Executor must not be null");
        CompletableFuture<T> result = new CompletableFuture<>();
        return result.completeAsync(toSupplier(callable, result), executor);
    }

    private static <T> Supplier<T> toSupplier(Callable<T> callable, CompletableFuture<T> result) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception ex) {
                result.completeExceptionally(ex instanceof CompletionException ? ex : new CompletionException(ex));
                return null;
            }
        };
    }
}
