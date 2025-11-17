package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/FutureAdapter.class */
public abstract class FutureAdapter<T, S> implements Future<T> {
    private final Future<S> adaptee;

    @Nullable
    private Object result;
    private State state = State.NEW;
    private final Object mutex = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/FutureAdapter$State.class */
    public enum State {
        NEW,
        SUCCESS,
        FAILURE
    }

    @Nullable
    protected abstract T adapt(S adapteeResult) throws ExecutionException;

    /* JADX INFO: Access modifiers changed from: protected */
    public FutureAdapter(Future<S> adaptee) {
        Assert.notNull(adaptee, "Delegate must not be null");
        this.adaptee = adaptee;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Future<S> getAdaptee() {
        return this.adaptee;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.adaptee.cancel(mayInterruptIfRunning);
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.adaptee.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.adaptee.isDone();
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get() throws InterruptedException, ExecutionException {
        return adaptInternal(this.adaptee.get());
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return adaptInternal(this.adaptee.get(timeout, unit));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0012. Please report as an issue. */
    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public final T adaptInternal(S adapteeResult) throws ExecutionException {
        T t;
        synchronized (this.mutex) {
            switch (this.state) {
                case SUCCESS:
                    t = this.result;
                    break;
                case FAILURE:
                    Assert.state(this.result instanceof ExecutionException, "Failure without exception");
                    throw ((ExecutionException) this.result);
                case NEW:
                    try {
                        T adapted = adapt(adapteeResult);
                        this.result = adapted;
                        this.state = State.SUCCESS;
                        t = adapted;
                        break;
                    } catch (ExecutionException ex) {
                        this.result = ex;
                        this.state = State.FAILURE;
                        throw ex;
                    } catch (Throwable ex2) {
                        ExecutionException execEx = new ExecutionException(ex2);
                        this.result = execEx;
                        this.state = State.FAILURE;
                        throw execEx;
                    }
                default:
                    throw new IncompatibleClassChangeError();
            }
        }
        return t;
    }
}
