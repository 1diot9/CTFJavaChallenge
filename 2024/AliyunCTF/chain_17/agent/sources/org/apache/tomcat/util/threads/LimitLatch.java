package org.apache.tomcat.util.threads;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/LimitLatch.class */
public class LimitLatch {
    private static final Log log = LogFactory.getLog((Class<?>) LimitLatch.class);
    private volatile long limit;
    private volatile boolean released = false;
    private final AtomicLong count = new AtomicLong(0);
    private final Sync sync = new Sync();

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/LimitLatch$Sync.class */
    private class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1;

        Sync() {
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected int tryAcquireShared(int ignored) {
            long newCount = LimitLatch.this.count.incrementAndGet();
            if (!LimitLatch.this.released && newCount > LimitLatch.this.limit) {
                LimitLatch.this.count.decrementAndGet();
                return -1;
            }
            return 1;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryReleaseShared(int arg) {
            LimitLatch.this.count.decrementAndGet();
            return true;
        }
    }

    public LimitLatch(long limit) {
        this.limit = limit;
    }

    public long getCount() {
        return this.count.get();
    }

    public long getLimit() {
        return this.limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void countUpOrAwait() throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Counting up[" + Thread.currentThread().getName() + "] latch=" + getCount());
        }
        this.sync.acquireSharedInterruptibly(1);
    }

    public long countDown() {
        this.sync.releaseShared(0);
        long result = getCount();
        if (log.isDebugEnabled()) {
            log.debug("Counting down[" + Thread.currentThread().getName() + "] latch=" + result);
        }
        return result;
    }

    public boolean releaseAll() {
        this.released = true;
        return this.sync.releaseShared(0);
    }

    public void reset() {
        this.count.set(0L);
        this.released = false;
    }

    public boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }

    public Collection<Thread> getQueuedThreads() {
        return this.sync.getQueuedThreads();
    }
}
