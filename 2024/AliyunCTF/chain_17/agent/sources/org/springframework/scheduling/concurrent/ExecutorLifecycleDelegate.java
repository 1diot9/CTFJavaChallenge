package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ExecutorLifecycleDelegate.class */
final class ExecutorLifecycleDelegate implements SmartLifecycle {
    private final ExecutorService executor;
    private volatile boolean paused;

    @Nullable
    private Runnable stopCallback;
    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unpaused = this.pauseLock.newCondition();
    private int executingTaskCount = 0;

    public ExecutorLifecycleDelegate(ExecutorService executor) {
        this.executor = executor;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        this.pauseLock.lock();
        try {
            this.paused = false;
            this.unpaused.signalAll();
        } finally {
            this.pauseLock.unlock();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        this.pauseLock.lock();
        try {
            this.paused = true;
            this.stopCallback = null;
        } finally {
            this.pauseLock.unlock();
        }
    }

    @Override // org.springframework.context.SmartLifecycle
    public void stop(Runnable callback) {
        this.pauseLock.lock();
        try {
            this.paused = true;
            if (this.executingTaskCount == 0) {
                this.stopCallback = null;
                callback.run();
            } else {
                this.stopCallback = callback;
            }
        } finally {
            this.pauseLock.unlock();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return (this.paused || this.executor.isTerminated()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void beforeExecute(Thread thread) {
        this.pauseLock.lock();
        while (this.paused && !this.executor.isShutdown()) {
            try {
                this.unpaused.await();
            } catch (InterruptedException e) {
                thread.interrupt();
                return;
            } finally {
                this.executingTaskCount++;
                this.pauseLock.unlock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void afterExecute() {
        Runnable callback;
        this.pauseLock.lock();
        try {
            this.executingTaskCount--;
            if (this.executingTaskCount == 0 && (callback = this.stopCallback) != null) {
                callback.run();
                this.stopCallback = null;
            }
        } finally {
            this.pauseLock.unlock();
        }
    }
}
