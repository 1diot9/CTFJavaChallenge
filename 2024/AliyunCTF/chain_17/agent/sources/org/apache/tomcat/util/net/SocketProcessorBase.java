package org.apache.tomcat.util.net;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketProcessorBase.class */
public abstract class SocketProcessorBase<S> implements Runnable {
    protected SocketWrapperBase<S> socketWrapper;
    protected SocketEvent event;

    protected abstract void doRun();

    public SocketProcessorBase(SocketWrapperBase<S> socketWrapper, SocketEvent event) {
        reset(socketWrapper, event);
    }

    public void reset(SocketWrapperBase<S> socketWrapper, SocketEvent event) {
        Objects.requireNonNull(event);
        this.socketWrapper = socketWrapper;
        this.event = event;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            if (this.socketWrapper.isClosed()) {
                return;
            }
            doRun();
        } finally {
            lock.unlock();
        }
    }
}
