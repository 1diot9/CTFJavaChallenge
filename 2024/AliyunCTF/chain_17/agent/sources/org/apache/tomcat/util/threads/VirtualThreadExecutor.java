package org.apache.tomcat.util.threads;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/VirtualThreadExecutor.class */
public class VirtualThreadExecutor extends AbstractExecutorService {
    private static final StringManager sm = StringManager.getManager((Class<?>) VirtualThreadExecutor.class);
    private CountDownLatch shutdown = new CountDownLatch(1);
    private final JreCompat jreCompat = JreCompat.getInstance();
    private Object threadBuilder;

    public VirtualThreadExecutor(String namePrefix) {
        this.threadBuilder = this.jreCompat.createVirtualThreadBuilder(namePrefix);
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        if (isShutdown()) {
            throw new RejectedExecutionException(sm.getString("virtualThreadExecutor.taskRejected", command.toString(), toString()));
        }
        this.jreCompat.threadBuilderStart(this.threadBuilder, command);
    }

    @Override // java.util.concurrent.ExecutorService
    public void shutdown() {
        this.shutdown.countDown();
    }

    @Override // java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return this.shutdown.getCount() == 0;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        return isShutdown();
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.shutdown.await(timeout, unit);
    }
}
