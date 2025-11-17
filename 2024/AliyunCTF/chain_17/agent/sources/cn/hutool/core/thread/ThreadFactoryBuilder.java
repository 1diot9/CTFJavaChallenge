package cn.hutool.core.thread;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.util.StrUtil;
import java.lang.Thread;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/ThreadFactoryBuilder.class */
public class ThreadFactoryBuilder implements Builder<ThreadFactory> {
    private static final long serialVersionUID = 1;
    private ThreadFactory backingThreadFactory;
    private String namePrefix;
    private Boolean daemon;
    private Integer priority;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static ThreadFactoryBuilder create() {
        return new ThreadFactoryBuilder();
    }

    public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = Boolean.valueOf(daemon);
        return this;
    }

    public ThreadFactoryBuilder setPriority(int priority) {
        if (priority < 1) {
            throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be >= {}", Integer.valueOf(priority), 1));
        }
        if (priority > 10) {
            throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be <= {}", Integer.valueOf(priority), 10));
        }
        this.priority = Integer.valueOf(priority);
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        ThreadFactory backingThreadFactory = null != builder.backingThreadFactory ? builder.backingThreadFactory : Executors.defaultThreadFactory();
        String namePrefix = builder.namePrefix;
        Boolean daemon = builder.daemon;
        Integer priority = builder.priority;
        Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
        AtomicLong count = null == namePrefix ? null : new AtomicLong();
        return r -> {
            Thread thread = backingThreadFactory.newThread(r);
            if (null != namePrefix) {
                thread.setName(namePrefix + count.getAndIncrement());
            }
            if (null != daemon) {
                thread.setDaemon(daemon.booleanValue());
            }
            if (null != priority) {
                thread.setPriority(priority.intValue());
            }
            if (null != handler) {
                thread.setUncaughtExceptionHandler(handler);
            }
            return thread;
        };
    }
}
