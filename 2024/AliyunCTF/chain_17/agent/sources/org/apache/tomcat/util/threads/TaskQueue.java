package org.apache.tomcat.util.threads;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/TaskQueue.class */
public class TaskQueue extends LinkedBlockingQueue<Runnable> {
    private static final long serialVersionUID = 1;
    protected static final StringManager sm = StringManager.getManager((Class<?>) TaskQueue.class);
    private volatile transient ThreadPoolExecutor parent;

    public TaskQueue() {
        this.parent = null;
    }

    public TaskQueue(int capacity) {
        super(capacity);
        this.parent = null;
    }

    public TaskQueue(Collection<? extends Runnable> c) {
        super(c);
        this.parent = null;
    }

    public void setParent(ThreadPoolExecutor tp) {
        this.parent = tp;
    }

    public boolean force(Runnable o) {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException(sm.getString("taskQueue.notRunning"));
        }
        return super.offer((TaskQueue) o);
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.Queue, java.util.concurrent.BlockingQueue
    public boolean offer(Runnable o) {
        if (this.parent == null) {
            return super.offer((TaskQueue) o);
        }
        if (this.parent.getPoolSizeNoLock() == this.parent.getMaximumPoolSize()) {
            return super.offer((TaskQueue) o);
        }
        if (this.parent.getSubmittedCount() <= this.parent.getPoolSizeNoLock()) {
            return super.offer((TaskQueue) o);
        }
        if (this.parent.getPoolSizeNoLock() < this.parent.getMaximumPoolSize()) {
            return false;
        }
        return super.offer((TaskQueue) o);
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.concurrent.BlockingQueue
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable runnable = (Runnable) super.poll(timeout, unit);
        if (runnable == null && this.parent != null) {
            this.parent.stopCurrentThreadIfNeeded();
        }
        return runnable;
    }

    @Override // java.util.concurrent.LinkedBlockingQueue, java.util.concurrent.BlockingQueue
    public Runnable take() throws InterruptedException {
        if (this.parent != null && this.parent.currentThreadShouldBeStopped()) {
            return poll(this.parent.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
        return (Runnable) super.take();
    }
}
