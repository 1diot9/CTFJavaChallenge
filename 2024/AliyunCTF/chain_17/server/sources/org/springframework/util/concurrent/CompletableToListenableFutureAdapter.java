package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/CompletableToListenableFutureAdapter.class */
public class CompletableToListenableFutureAdapter<T> implements ListenableFuture<T> {
    private final CompletableFuture<T> completableFuture;
    private final ListenableFutureCallbackRegistry<T> callbacks;

    public CompletableToListenableFutureAdapter(CompletionStage<T> completionStage) {
        this((CompletableFuture) completionStage.toCompletableFuture());
    }

    public CompletableToListenableFutureAdapter(CompletableFuture<T> completableFuture) {
        this.callbacks = new ListenableFutureCallbackRegistry<>();
        this.completableFuture = completableFuture;
        this.completableFuture.whenComplete((BiConsumer) (result, ex) -> {
            if (ex != null) {
                this.callbacks.failure(ex);
            } else {
                this.callbacks.success(result);
            }
        });
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        this.callbacks.addCallback(callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
        this.callbacks.addSuccessCallback(successCallback);
        this.callbacks.addFailureCallback(failureCallback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public CompletableFuture<T> completable() {
        return this.completableFuture;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.completableFuture.cancel(mayInterruptIfRunning);
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.completableFuture.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.completableFuture.isDone();
    }

    @Override // java.util.concurrent.Future
    public T get() throws InterruptedException, ExecutionException {
        return this.completableFuture.get();
    }

    @Override // java.util.concurrent.Future
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.completableFuture.get(timeout, unit);
    }
}
