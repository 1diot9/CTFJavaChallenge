package org.springframework.util.concurrent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/ListenableFuture.class */
public interface ListenableFuture<T> extends Future<T> {
    @Deprecated(since = "6.0")
    void addCallback(ListenableFutureCallback<? super T> callback);

    @Deprecated(since = "6.0")
    void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback);

    default CompletableFuture<T> completable() {
        CompletableFuture<T> completable = new DelegatingCompletableFuture<>(this);
        Objects.requireNonNull(completable);
        SuccessCallback<? super T> successCallback = completable::complete;
        Objects.requireNonNull(completable);
        addCallback(successCallback, completable::completeExceptionally);
        return completable;
    }
}
