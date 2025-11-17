package org.apache.juli;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LogRecord;
import org.apache.juli.FileHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/AsyncFileHandler.class */
public class AsyncFileHandler extends FileHandler {
    static final String THREAD_PREFIX = "AsyncFileHandlerWriter-";
    public static final int OVERFLOW_DROP_LAST = 1;
    public static final int OVERFLOW_DROP_FIRST = 2;
    public static final int OVERFLOW_DROP_FLUSH = 3;
    public static final int OVERFLOW_DROP_CURRENT = 4;
    public static final int DEFAULT_OVERFLOW_DROP_TYPE = 1;
    public static final int DEFAULT_MAX_RECORDS = 10000;
    public static final int OVERFLOW_DROP_TYPE = Integer.parseInt(System.getProperty("org.apache.juli.AsyncOverflowDropType", Integer.toString(1)));
    public static final int MAX_RECORDS = Integer.parseInt(System.getProperty("org.apache.juli.AsyncMaxRecordCount", Integer.toString(10000)));
    private static final LoggerExecutorService LOGGER_SERVICE = new LoggerExecutorService(OVERFLOW_DROP_TYPE, MAX_RECORDS);
    private final Object closeLock;
    protected volatile boolean closed;
    private final LoggerExecutorService loggerService;

    public AsyncFileHandler() {
        this(null, null, null);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix) {
        this(directory, prefix, suffix, null);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix, Integer maxDays) {
        this(directory, prefix, suffix, maxDays, LOGGER_SERVICE);
    }

    AsyncFileHandler(String directory, String prefix, String suffix, Integer maxDays, LoggerExecutorService loggerService) {
        super(directory, prefix, suffix, maxDays);
        this.closeLock = new Object();
        this.closed = false;
        loggerService.registerHandler();
        this.loggerService = loggerService;
    }

    @Override // org.apache.juli.FileHandler, java.util.logging.Handler
    public void close() {
        if (this.closed) {
            return;
        }
        synchronized (this.closeLock) {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.loggerService.deregisterHandler();
            super.close();
        }
    }

    @Override // org.apache.juli.FileHandler
    public void open() {
        if (!this.closed) {
            return;
        }
        synchronized (this.closeLock) {
            if (this.closed) {
                this.closed = false;
                this.loggerService.registerHandler();
                super.open();
            }
        }
    }

    @Override // org.apache.juli.FileHandler, java.util.logging.Handler
    public void publish(final LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        record.getSourceMethodName();
        this.loggerService.execute(new Runnable() { // from class: org.apache.juli.AsyncFileHandler.1
            @Override // java.lang.Runnable
            public void run() {
                if (!AsyncFileHandler.this.closed || AsyncFileHandler.this.loggerService.isTerminating()) {
                    AsyncFileHandler.this.publishInternal(record);
                }
            }
        });
    }

    protected void publishInternal(LogRecord record) {
        super.publish(record);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/AsyncFileHandler$LoggerExecutorService.class */
    static class LoggerExecutorService extends ThreadPoolExecutor {
        private static final FileHandler.ThreadFactory THREAD_FACTORY = new FileHandler.ThreadFactory(AsyncFileHandler.THREAD_PREFIX);
        private final AtomicInteger handlerCount;

        LoggerExecutorService(int overflowDropType, int maxRecords) {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(maxRecords), THREAD_FACTORY);
            this.handlerCount = new AtomicInteger();
            switch (overflowDropType) {
                case 1:
                default:
                    setRejectedExecutionHandler(new DropLastPolicy());
                    return;
                case 2:
                    setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
                    return;
                case 3:
                    setRejectedExecutionHandler(new DropFlushPolicy());
                    return;
                case 4:
                    setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
                    return;
            }
        }

        @Override // java.util.concurrent.ThreadPoolExecutor
        public LinkedBlockingDeque<Runnable> getQueue() {
            return (LinkedBlockingDeque) super.getQueue();
        }

        public void registerHandler() {
            this.handlerCount.incrementAndGet();
        }

        public void deregisterHandler() {
            int newCount = this.handlerCount.decrementAndGet();
            if (newCount == 0) {
                try {
                    Thread dummyHook = new Thread();
                    Runtime.getRuntime().addShutdownHook(dummyHook);
                    Runtime.getRuntime().removeShutdownHook(dummyHook);
                } catch (IllegalStateException e) {
                    shutdown();
                    try {
                        awaitTermination(10L, TimeUnit.SECONDS);
                    } catch (InterruptedException e2) {
                    }
                    shutdownNow();
                }
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/AsyncFileHandler$DropFlushPolicy.class */
    private static class DropFlushPolicy implements RejectedExecutionHandler {
        private DropFlushPolicy() {
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            while (!executor.isShutdown()) {
                try {
                    if (executor.getQueue().offer(r, 1000L, TimeUnit.MILLISECONDS)) {
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Interrupted", e);
                }
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/AsyncFileHandler$DropLastPolicy.class */
    private static class DropLastPolicy implements RejectedExecutionHandler {
        private DropLastPolicy() {
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                ((LoggerExecutorService) executor).getQueue().pollLast();
                executor.execute(r);
            }
        }
    }
}
