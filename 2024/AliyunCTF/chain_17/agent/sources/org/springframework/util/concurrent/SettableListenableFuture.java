package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/SettableListenableFuture.class */
public class SettableListenableFuture<T> implements ListenableFuture<T> {
    private static final Callable<Object> DUMMY_CALLABLE = () -> {
        throw new IllegalStateException("Should never be called");
    };
    private final SettableTask<T> settableTask = new SettableTask<>();

    public boolean set(@Nullable T value) {
        return this.settableTask.setResultValue(value);
    }

    public boolean setException(Throwable exception) {
        Assert.notNull(exception, "Exception must not be null");
        return this.settableTask.setExceptionResult(exception);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        this.settableTask.addCallback(callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
        this.settableTask.addCallback(successCallback, failureCallback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public CompletableFuture<T> completable() {
        return this.settableTask.completable();
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = this.settableTask.cancel(mayInterruptIfRunning);
        if (cancelled && mayInterruptIfRunning) {
            interruptTask();
        }
        return cancelled;
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.settableTask.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.settableTask.isDone();
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get() throws InterruptedException, ExecutionException {
        return (T) this.settableTask.get();
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T) this.settableTask.get(j, timeUnit);
    }

    protected void interruptTask() {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/SettableListenableFuture$SettableTask.class */
    private static class SettableTask<T> extends ListenableFutureTask<T> {

        @Nullable
        private volatile Thread completingThread;

        public SettableTask() {
            super(SettableListenableFuture.DUMMY_CALLABLE);
        }

        public boolean setResultValue(@Nullable T value) {
            set(value);
            return checkCompletingThread();
        }

        public boolean setExceptionResult(Throwable exception) {
            setException(exception);
            return checkCompletingThread();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.concurrent.ListenableFutureTask, java.util.concurrent.FutureTask
        public void done() {
            if (!isCancelled()) {
                this.completingThread = Thread.currentThread();
            }
            super.done();
        }

        private boolean checkCompletingThread() {
            boolean check = this.completingThread == Thread.currentThread();
            if (check) {
                this.completingThread = null;
            }
            return check;
        }
    }
}
