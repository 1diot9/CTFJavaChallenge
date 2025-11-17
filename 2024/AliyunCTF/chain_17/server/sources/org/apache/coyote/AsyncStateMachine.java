package org.apache.coyote;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AsyncStateMachine.class */
public class AsyncStateMachine {
    private static final Log log = LogFactory.getLog((Class<?>) AsyncStateMachine.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) AsyncStateMachine.class);
    private volatile AsyncState state = AsyncState.DISPATCHED;
    private volatile long lastAsyncStart = 0;
    private final AtomicLong generation = new AtomicLong(0);
    private boolean hasProcessedError = false;
    private AsyncContextCallback asyncCtxt = null;
    private final AbstractProcessor processor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AsyncStateMachine$AsyncState.class */
    public enum AsyncState {
        DISPATCHED(false, false, false, false),
        STARTING(true, true, false, false),
        STARTED(true, true, false, false),
        MUST_COMPLETE(true, true, true, false),
        COMPLETE_PENDING(true, true, false, false),
        COMPLETING(true, false, true, false),
        TIMING_OUT(true, true, false, false),
        MUST_DISPATCH(true, true, false, true),
        DISPATCH_PENDING(true, true, false, false),
        DISPATCHING(true, false, false, true),
        READ_WRITE_OP(true, true, false, false),
        MUST_ERROR(true, true, false, false),
        ERROR(true, true, false, false);

        private final boolean isAsync;
        private final boolean isStarted;
        private final boolean isCompleting;
        private final boolean isDispatching;

        AsyncState(boolean isAsync, boolean isStarted, boolean isCompleting, boolean isDispatching) {
            this.isAsync = isAsync;
            this.isStarted = isStarted;
            this.isCompleting = isCompleting;
            this.isDispatching = isDispatching;
        }

        boolean isAsync() {
            return this.isAsync;
        }

        boolean isStarted() {
            return this.isStarted;
        }

        boolean isDispatching() {
            return this.isDispatching;
        }

        boolean isCompleting() {
            return this.isCompleting;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsyncStateMachine(AbstractProcessor processor) {
        this.processor = processor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsync() {
        return this.state.isAsync();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncDispatching() {
        return this.state.isDispatching();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncStarted() {
        return this.state.isStarted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncTimingOut() {
        return this.state == AsyncState.TIMING_OUT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncError() {
        return this.state == AsyncState.ERROR;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCompleting() {
        return this.state.isCompleting();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastAsyncStart() {
        return this.lastAsyncStart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getCurrentGeneration() {
        return this.generation.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncStart(AsyncContextCallback asyncCtxt) {
        if (this.state == AsyncState.DISPATCHED) {
            this.generation.incrementAndGet();
            updateState(AsyncState.STARTING);
            this.asyncCtxt = asyncCtxt;
            this.lastAsyncStart = System.currentTimeMillis();
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncStart()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncOperation() {
        if (this.state == AsyncState.STARTED) {
            updateState(AsyncState.READ_WRITE_OP);
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncOperation()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        if (this.state == AsyncState.COMPLETE_PENDING) {
            clearNonBlockingListeners();
            updateState(AsyncState.COMPLETING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCH_PENDING) {
            clearNonBlockingListeners();
            updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP) {
            updateState(AsyncState.STARTED);
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        if (this.state == AsyncState.MUST_COMPLETE || this.state == AsyncState.COMPLETING) {
            this.asyncCtxt.fireOnComplete();
            updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.MUST_DISPATCH) {
            updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCHING) {
            this.asyncCtxt.fireOnComplete();
            updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTED) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncPostProcess()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncComplete() {
        Request request = this.processor.getRequest();
        if ((request == null || !request.isRequestThread()) && (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP)) {
            updateState(AsyncState.COMPLETE_PENDING);
            return false;
        }
        clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            updateState(AsyncState.MUST_COMPLETE);
        } else if (this.state == AsyncState.STARTED) {
            updateState(AsyncState.COMPLETING);
            triggerDispatch = true;
        } else if (this.state == AsyncState.READ_WRITE_OP || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR) {
            updateState(AsyncState.COMPLETING);
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncComplete()", this.state));
        }
        return triggerDispatch;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncTimeout() {
        if (this.state == AsyncState.STARTED) {
            updateState(AsyncState.TIMING_OUT);
            return true;
        }
        if (this.state == AsyncState.COMPLETING || this.state == AsyncState.DISPATCHING || this.state == AsyncState.DISPATCHED) {
            return false;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncTimeout()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncDispatch() {
        Request request = this.processor.getRequest();
        if ((request == null || !request.isRequestThread()) && (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP)) {
            updateState(AsyncState.DISPATCH_PENDING);
            return false;
        }
        clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            updateState(AsyncState.MUST_DISPATCH);
        } else if (this.state == AsyncState.STARTED) {
            updateState(AsyncState.DISPATCHING);
            triggerDispatch = true;
        } else if (this.state == AsyncState.READ_WRITE_OP || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR) {
            updateState(AsyncState.DISPATCHING);
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncDispatch()", this.state));
        }
        return triggerDispatch;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncDispatched() {
        if (this.state == AsyncState.DISPATCHING || this.state == AsyncState.MUST_DISPATCH) {
            updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncDispatched()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncError() {
        Request request = this.processor.getRequest();
        boolean containerThread = request != null && request.isRequestThread();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("asyncStateMachine.asyncError.start"));
        }
        clearNonBlockingListeners();
        if (this.state == AsyncState.STARTING) {
            updateState(AsyncState.MUST_ERROR);
        } else {
            if (this.hasProcessedError) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("asyncStateMachine.asyncError.skip"));
                    return false;
                }
                return false;
            }
            this.hasProcessedError = true;
            if (this.state == AsyncState.DISPATCHED) {
                this.asyncCtxt.incrementInProgressAsyncCount();
                updateState(AsyncState.ERROR);
            } else {
                updateState(AsyncState.ERROR);
            }
        }
        return !containerThread;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncRun(Runnable runnable) {
        ClassLoader oldCL;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.STARTED || this.state == AsyncState.READ_WRITE_OP) {
            Thread currentThread = Thread.currentThread();
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl(currentThread);
                oldCL = (ClassLoader) AccessController.doPrivileged(pa);
            } else {
                oldCL = currentThread.getContextClassLoader();
            }
            try {
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa2 = new PrivilegedSetTccl(currentThread, getClass().getClassLoader());
                    AccessController.doPrivileged(pa2);
                } else {
                    currentThread.setContextClassLoader(getClass().getClassLoader());
                }
                this.processor.execute(runnable);
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa3 = new PrivilegedSetTccl(currentThread, oldCL);
                    AccessController.doPrivileged(pa3);
                    return;
                } else {
                    currentThread.setContextClassLoader(oldCL);
                    return;
                }
            } catch (Throwable th) {
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa4 = new PrivilegedSetTccl(currentThread, oldCL);
                    AccessController.doPrivileged(pa4);
                } else {
                    currentThread.setContextClassLoader(oldCL);
                }
                throw th;
            }
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncRun()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isAvailable() {
        if (this.asyncCtxt == null) {
            return false;
        }
        return this.asyncCtxt.isAvailable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void recycle() {
        if (this.lastAsyncStart == 0) {
            return;
        }
        notifyAll();
        this.asyncCtxt = null;
        this.state = AsyncState.DISPATCHED;
        this.lastAsyncStart = 0L;
        this.hasProcessedError = false;
    }

    private void clearNonBlockingListeners() {
        this.processor.getRequest().listener = null;
        this.processor.getRequest().getResponse().listener = null;
    }

    private synchronized void updateState(AsyncState newState) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("asyncStateMachine.stateChange", this.state, newState));
        }
        this.state = newState;
    }
}
