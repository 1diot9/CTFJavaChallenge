package org.apache.coyote.http2;

import java.util.concurrent.TimeUnit;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/WindowAllocationManager.class */
public class WindowAllocationManager {
    private static final Log log = LogFactory.getLog((Class<?>) WindowAllocationManager.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) WindowAllocationManager.class);
    private static final int NONE = 0;
    private static final int STREAM = 1;
    private static final int CONNECTION = 2;
    private final Stream stream;
    private int waitingFor = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowAllocationManager(Stream stream) {
        this.stream = stream;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForStream(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitFor.stream", this.stream.getConnectionId(), this.stream.getIdAsString(), Long.toString(timeout)));
        }
        waitFor(1, timeout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForConnection(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitFor.connection", this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.stream.getConnectionAllocationRequested()), Long.toString(timeout)));
        }
        waitFor(2, timeout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForStreamNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitForNonBlocking.stream", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        waitForNonBlocking(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForConnectionNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("windowAllocationManager.waitForNonBlocking.connection", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        waitForNonBlocking(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyStream() {
        notify(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyConnection() {
        notify(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyAny() {
        notify(3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isWaitingForStream() {
        return isWaitingFor(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isWaitingForConnection() {
        return isWaitingFor(2);
    }

    private boolean isWaitingFor(int waitTarget) {
        this.stream.windowAllocationLock.lock();
        try {
            return (this.waitingFor & waitTarget) > 0;
        } finally {
            this.stream.windowAllocationLock.unlock();
        }
    }

    private void waitFor(int waitTarget, long timeout) throws InterruptedException {
        long timeoutRemaining;
        this.stream.windowAllocationLock.lock();
        try {
            if (this.waitingFor != 0) {
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", this.stream.getConnectionId(), this.stream.getIdAsString()));
            }
            this.waitingFor = waitTarget;
            long startNanos = -1;
            do {
                if (timeout < 0) {
                    this.stream.windowAllocationAvailable.await();
                } else {
                    if (startNanos == -1) {
                        startNanos = System.nanoTime();
                        timeoutRemaining = timeout;
                    } else {
                        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                        if (elapsedMillis == 0) {
                            elapsedMillis = 1;
                        }
                        timeoutRemaining = timeout - elapsedMillis;
                        if (timeoutRemaining <= 0) {
                            return;
                        }
                    }
                    this.stream.windowAllocationAvailable.await(timeoutRemaining, TimeUnit.MILLISECONDS);
                }
            } while (this.waitingFor != 0);
            this.stream.windowAllocationLock.unlock();
        } finally {
            this.stream.windowAllocationLock.unlock();
        }
    }

    private void waitForNonBlocking(int waitTarget) {
        this.stream.windowAllocationLock.lock();
        try {
            if (this.waitingFor == 0) {
                this.waitingFor = waitTarget;
            } else if (this.waitingFor != waitTarget) {
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", this.stream.getConnectionId(), this.stream.getIdAsString()));
            }
        } finally {
            this.stream.windowAllocationLock.unlock();
        }
    }

    private void notify(int notifyTarget) {
        this.stream.windowAllocationLock.lock();
        try {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("windowAllocationManager.notify", this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.waitingFor), Integer.toString(notifyTarget)));
            }
            if ((notifyTarget & this.waitingFor) > 0) {
                this.waitingFor = 0;
                Response response = this.stream.getCoyoteResponse();
                if (response != null) {
                    if (response.getWriteListener() == null) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("windowAllocationManager.notified", this.stream.getConnectionId(), this.stream.getIdAsString()));
                        }
                        this.stream.windowAllocationAvailable.signal();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("windowAllocationManager.dispatched", this.stream.getConnectionId(), this.stream.getIdAsString()));
                        }
                        response.action(ActionCode.DISPATCH_WRITE, null);
                        response.action(ActionCode.DISPATCH_EXECUTE, null);
                    }
                }
            }
        } finally {
            this.stream.windowAllocationLock.unlock();
        }
    }
}
