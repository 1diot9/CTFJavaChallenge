package org.springframework.util.concurrent;

import java.util.ArrayDeque;
import java.util.Queue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/ListenableFutureCallbackRegistry.class */
public class ListenableFutureCallbackRegistry<T> {

    @Nullable
    private Object result;
    private final Queue<SuccessCallback<? super T>> successCallbacks = new ArrayDeque(1);
    private final Queue<FailureCallback> failureCallbacks = new ArrayDeque(1);
    private State state = State.NEW;
    private final Object mutex = new Object();

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/ListenableFutureCallbackRegistry$State.class */
    private enum State {
        NEW,
        SUCCESS,
        FAILURE
    }

    public void addCallback(ListenableFutureCallback<? super T> callback) {
        Assert.notNull(callback, "'callback' must not be null");
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW:
                    this.successCallbacks.add(callback);
                    this.failureCallbacks.add(callback);
                    break;
                case SUCCESS:
                    notifySuccess(callback);
                    break;
                case FAILURE:
                    notifyFailure(callback);
                    break;
            }
        }
    }

    private void notifySuccess(SuccessCallback<? super T> successCallback) {
        try {
            successCallback.onSuccess((Object) this.result);
        } catch (Throwable th) {
        }
    }

    private void notifyFailure(FailureCallback callback) {
        Assert.state(this.result instanceof Throwable, "No Throwable result for failure state");
        try {
            callback.onFailure((Throwable) this.result);
        } catch (Throwable th) {
        }
    }

    public void addSuccessCallback(SuccessCallback<? super T> callback) {
        Assert.notNull(callback, "'callback' must not be null");
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW:
                    this.successCallbacks.add(callback);
                    break;
                case SUCCESS:
                    notifySuccess(callback);
                    break;
            }
        }
    }

    public void addFailureCallback(FailureCallback callback) {
        Assert.notNull(callback, "'callback' must not be null");
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW:
                    this.failureCallbacks.add(callback);
                    break;
                case FAILURE:
                    notifyFailure(callback);
                    break;
            }
        }
    }

    public void success(@Nullable T result) {
        synchronized (this.mutex) {
            this.state = State.SUCCESS;
            this.result = result;
            while (true) {
                SuccessCallback<? super T> callback = this.successCallbacks.poll();
                if (callback != null) {
                    notifySuccess(callback);
                }
            }
        }
    }

    public void failure(Throwable ex) {
        synchronized (this.mutex) {
            this.state = State.FAILURE;
            this.result = ex;
            while (true) {
                FailureCallback callback = this.failureCallbacks.poll();
                if (callback != null) {
                    notifyFailure(callback);
                }
            }
        }
    }
}
