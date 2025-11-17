package cn.hutool.core.thread;

import cn.hutool.core.util.StrUtil;
import java.lang.Thread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/NamedThreadFactory.class */
public class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final boolean isDaemon;
    private final Thread.UncaughtExceptionHandler handler;

    public NamedThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, null, isDaemon);
    }

    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
        this(prefix, threadGroup, isDaemon, null);
    }

    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon, Thread.UncaughtExceptionHandler handler) {
        this.threadNumber = new AtomicInteger(1);
        this.prefix = StrUtil.isBlank(prefix) ? "Hutool" : prefix;
        this.group = null == threadGroup ? ThreadUtil.currentThreadGroup() : threadGroup;
        this.isDaemon = isDaemon;
        this.handler = handler;
    }

    @Override // java.util.concurrent.ThreadFactory
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, StrUtil.format("{}{}", this.prefix, Integer.valueOf(this.threadNumber.getAndIncrement())));
        if (false == t.isDaemon()) {
            if (this.isDaemon) {
                t.setDaemon(true);
            }
        } else if (false == this.isDaemon) {
            t.setDaemon(false);
        }
        if (null != this.handler) {
            t.setUncaughtExceptionHandler(this.handler);
        }
        if (5 != t.getPriority()) {
            t.setPriority(5);
        }
        return t;
    }
}
