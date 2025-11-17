package org.h2.util;

import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Task.class */
public abstract class Task implements Runnable {
    private static final AtomicInteger counter = new AtomicInteger();
    public volatile boolean stop;
    private volatile Object result;
    private volatile boolean finished;
    private Thread thread;
    private volatile Exception ex;

    public abstract void call() throws Exception;

    @Override // java.lang.Runnable
    public void run() {
        try {
            call();
        } catch (Exception e) {
            this.ex = e;
        }
        this.finished = true;
    }

    public Task execute() {
        return execute(getClass().getName() + ":" + counter.getAndIncrement());
    }

    public Task execute(String str) {
        this.thread = new Thread(this, str);
        this.thread.setDaemon(true);
        this.thread.start();
        return this;
    }

    public Object get() {
        Exception exception = getException();
        if (exception != null) {
            throw new RuntimeException(exception);
        }
        return this.result;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Exception getException() {
        join();
        if (this.ex != null) {
            return this.ex;
        }
        return null;
    }

    public void join() {
        this.stop = true;
        if (this.thread == null) {
            throw new IllegalStateException("Thread not started");
        }
        try {
            this.thread.join();
        } catch (InterruptedException e) {
        }
    }
}
