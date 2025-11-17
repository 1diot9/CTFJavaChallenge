package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/DelegatingCompletableFuture.class */
class DelegatingCompletableFuture<T> extends CompletableFuture<T> {
    private final Future<T> delegate;

    public DelegatingCompletableFuture(Future<T> delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    @Override // java.util.concurrent.CompletableFuture, java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = this.delegate.cancel(mayInterruptIfRunning);
        super.cancel(mayInterruptIfRunning);
        return result;
    }
}
