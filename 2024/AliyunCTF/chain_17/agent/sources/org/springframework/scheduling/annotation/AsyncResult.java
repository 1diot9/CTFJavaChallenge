package org.springframework.scheduling.annotation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AsyncResult.class */
public class AsyncResult<V> implements ListenableFuture<V> {

    @Nullable
    private final V value;

    @Nullable
    private final Throwable executionException;

    public AsyncResult(@Nullable V value) {
        this(value, null);
    }

    private AsyncResult(@Nullable V value, @Nullable Throwable ex) {
        this.value = value;
        this.executionException = ex;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return false;
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return true;
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public V get() throws ExecutionException {
        if (this.executionException != null) {
            Throwable th = this.executionException;
            if (!(th instanceof ExecutionException)) {
                throw new ExecutionException(this.executionException);
            }
            ExecutionException execEx = (ExecutionException) th;
            throw execEx;
        }
        return this.value;
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public V get(long timeout, TimeUnit unit) throws ExecutionException {
        return get();
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super V> callback) {
        addCallback(callback, callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super V> successCallback, FailureCallback failureCallback) {
        try {
            if (this.executionException != null) {
                failureCallback.onFailure(exposedException(this.executionException));
            } else {
                successCallback.onSuccess(this.value);
            }
        } catch (Throwable th) {
        }
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public CompletableFuture<V> completable() {
        if (this.executionException != null) {
            CompletableFuture<V> completable = new CompletableFuture<>();
            completable.completeExceptionally(exposedException(this.executionException));
            return completable;
        }
        return CompletableFuture.completedFuture(this.value);
    }

    public static <V> ListenableFuture<V> forValue(V value) {
        return new AsyncResult(value, null);
    }

    public static <V> ListenableFuture<V> forExecutionException(Throwable ex) {
        return new AsyncResult(null, ex);
    }

    private static Throwable exposedException(Throwable original) {
        Throwable cause;
        if ((original instanceof ExecutionException) && (cause = original.getCause()) != null) {
            return cause;
        }
        return original;
    }
}
