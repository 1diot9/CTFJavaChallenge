package org.apache.catalina.core;

import java.util.concurrent.TimeUnit;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ResizableExecutor;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardThreadExecutor.class */
public class StandardThreadExecutor extends LifecycleMBeanBase implements Executor, ResizableExecutor {
    protected static final StringManager sm = StringManager.getManager((Class<?>) StandardThreadExecutor.class);
    protected String name;
    protected int threadPriority = 5;
    protected boolean daemon = true;
    protected String namePrefix = "tomcat-exec-";
    protected int maxThreads = 200;
    protected int minSpareThreads = 25;
    protected int maxIdleTime = org.apache.coyote.http11.Constants.DEFAULT_CONNECTION_TIMEOUT;
    protected ThreadPoolExecutor executor = null;
    protected int maxQueueSize = Integer.MAX_VALUE;
    protected long threadRenewalDelay = 1000;
    private TaskQueue taskqueue = null;

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        this.taskqueue = new TaskQueue(this.maxQueueSize);
        TaskThreadFactory tf = new TaskThreadFactory(this.namePrefix, this.daemon, getThreadPriority());
        this.executor = new ThreadPoolExecutor(getMinSpareThreads(), getMaxThreads(), this.maxIdleTime, TimeUnit.MILLISECONDS, this.taskqueue, tf);
        this.executor.setThreadRenewalDelay(this.threadRenewalDelay);
        this.taskqueue.setParent(this.executor);
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = null;
        this.taskqueue = null;
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        if (this.executor != null) {
            this.executor.execute(command);
            return;
        }
        throw new IllegalStateException(sm.getString("standardThreadExecutor.notStarted"));
    }

    public void contextStopping() {
        if (this.executor != null) {
            this.executor.contextStopping();
        }
    }

    public int getThreadPriority() {
        return this.threadPriority;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    @Override // org.apache.tomcat.util.threads.ResizableExecutor
    public int getMaxThreads() {
        return this.maxThreads;
    }

    public int getMinSpareThreads() {
        return this.minSpareThreads;
    }

    @Override // org.apache.catalina.Executor
    public String getName() {
        return this.name;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        if (this.executor != null) {
            this.executor.setKeepAliveTime(maxIdleTime, TimeUnit.MILLISECONDS);
        }
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        if (this.executor != null) {
            this.executor.setMaximumPoolSize(maxThreads);
        }
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        if (this.executor != null) {
            this.executor.setCorePoolSize(minSpareThreads);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxQueueSize(int size) {
        this.maxQueueSize = size;
    }

    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
        if (this.executor != null) {
            this.executor.setThreadRenewalDelay(threadRenewalDelay);
        }
    }

    @Override // org.apache.tomcat.util.threads.ResizableExecutor
    public int getActiveCount() {
        if (this.executor != null) {
            return this.executor.getActiveCount();
        }
        return 0;
    }

    public long getCompletedTaskCount() {
        if (this.executor != null) {
            return this.executor.getCompletedTaskCount();
        }
        return 0L;
    }

    public int getCorePoolSize() {
        if (this.executor != null) {
            return this.executor.getCorePoolSize();
        }
        return 0;
    }

    public int getLargestPoolSize() {
        if (this.executor != null) {
            return this.executor.getLargestPoolSize();
        }
        return 0;
    }

    @Override // org.apache.tomcat.util.threads.ResizableExecutor
    public int getPoolSize() {
        if (this.executor != null) {
            return this.executor.getPoolSize();
        }
        return 0;
    }

    public int getQueueSize() {
        if (this.executor != null) {
            return this.executor.getQueue().size();
        }
        return -1;
    }

    @Override // org.apache.tomcat.util.threads.ResizableExecutor
    public boolean resizePool(int corePoolSize, int maximumPoolSize) {
        if (this.executor == null) {
            return false;
        }
        this.executor.setCorePoolSize(corePoolSize);
        this.executor.setMaximumPoolSize(maximumPoolSize);
        return true;
    }

    @Override // org.apache.tomcat.util.threads.ResizableExecutor
    public boolean resizeQueue(int capacity) {
        return false;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return null;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Executor,name=" + getName();
    }
}
