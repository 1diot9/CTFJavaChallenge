package org.apache.tomcat.util.threads;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/InlineExecutorService.class */
public class InlineExecutorService extends AbstractExecutorService {
    private volatile boolean shutdown;
    private volatile boolean taskRunning;
    private volatile boolean terminated;
    private final Object lock = new Object();

    @Override // java.util.concurrent.ExecutorService
    public void shutdown() {
        this.shutdown = true;
        synchronized (this.lock) {
            this.terminated = !this.taskRunning;
        }
    }

    @Override // java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        shutdown();
        return null;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return this.shutdown;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        return this.terminated;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (this.lock) {
            if (this.terminated) {
                return true;
            }
            this.lock.wait(unit.toMillis(timeout));
            return this.terminated;
        }
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        synchronized (this.lock) {
            if (this.shutdown) {
                throw new RejectedExecutionException();
            }
            this.taskRunning = true;
        }
        command.run();
        synchronized (this.lock) {
            this.taskRunning = false;
            if (this.shutdown) {
                this.terminated = true;
                this.lock.notifyAll();
            }
        }
    }
}
