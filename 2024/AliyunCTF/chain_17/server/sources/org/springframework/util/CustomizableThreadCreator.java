package org.springframework.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/CustomizableThreadCreator.class */
public class CustomizableThreadCreator implements Serializable {
    private String threadNamePrefix;
    private int threadPriority;
    private boolean daemon;

    @Nullable
    private ThreadGroup threadGroup;
    private final AtomicInteger threadCount;

    public CustomizableThreadCreator() {
        this.threadPriority = 5;
        this.daemon = false;
        this.threadCount = new AtomicInteger();
        this.threadNamePrefix = getDefaultThreadNamePrefix();
    }

    public CustomizableThreadCreator(@Nullable String threadNamePrefix) {
        this.threadPriority = 5;
        this.daemon = false;
        this.threadCount = new AtomicInteger();
        this.threadNamePrefix = threadNamePrefix != null ? threadNamePrefix : getDefaultThreadNamePrefix();
    }

    public void setThreadNamePrefix(@Nullable String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix != null ? threadNamePrefix : getDefaultThreadNamePrefix();
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public int getThreadPriority() {
        return this.threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public void setThreadGroupName(String name) {
        this.threadGroup = new ThreadGroup(name);
    }

    public void setThreadGroup(@Nullable ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    @Nullable
    public ThreadGroup getThreadGroup() {
        return this.threadGroup;
    }

    public Thread createThread(Runnable runnable) {
        Thread thread = new Thread(getThreadGroup(), runnable, nextThreadName());
        thread.setPriority(getThreadPriority());
        thread.setDaemon(isDaemon());
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String nextThreadName() {
        return getThreadNamePrefix() + this.threadCount.incrementAndGet();
    }

    protected String getDefaultThreadNamePrefix() {
        return ClassUtils.getShortName(getClass()) + "-";
    }
}
