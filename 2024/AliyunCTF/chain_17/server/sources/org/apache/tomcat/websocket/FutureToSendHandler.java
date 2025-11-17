package org.apache.tomcat.websocket;

import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/FutureToSendHandler.class */
public class FutureToSendHandler implements Future<Void>, SendHandler {
    private static final StringManager sm = StringManager.getManager((Class<?>) FutureToSendHandler.class);
    private final WsSession wsSession;
    private final CountDownLatch latch = new CountDownLatch(1);
    private volatile AtomicReference<SendResult> result = new AtomicReference<>(null);

    /* JADX INFO: Access modifiers changed from: package-private */
    public FutureToSendHandler(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    @Override // jakarta.websocket.SendHandler
    public void onResult(SendResult result) {
        this.result.compareAndSet(null, result);
        this.latch.countDown();
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
        return this.latch.getCount() == 0;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Future
    public Void get() throws InterruptedException, ExecutionException {
        try {
            this.wsSession.registerFuture(this);
            this.latch.await();
            if (this.result.get().getException() != null) {
                throw new ExecutionException(this.result.get().getException());
            }
            return null;
        } finally {
            this.wsSession.unregisterFuture(this);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Future
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            this.wsSession.registerFuture(this);
            boolean retval = this.latch.await(timeout, unit);
            this.wsSession.unregisterFuture(this);
            if (!retval) {
                throw new TimeoutException(sm.getString("futureToSendHandler.timeout", Long.valueOf(timeout), unit.toString().toLowerCase()));
            }
            if (this.result.get().getException() != null) {
                throw new ExecutionException(this.result.get().getException());
            }
            return null;
        } catch (Throwable th) {
            this.wsSession.unregisterFuture(this);
            throw th;
        }
    }
}
