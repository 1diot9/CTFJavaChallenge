package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelGroupUtil.class */
public class AsyncChannelGroupUtil {
    private static final StringManager sm = StringManager.getManager((Class<?>) AsyncChannelGroupUtil.class);
    private static AsynchronousChannelGroup group = null;
    private static int usageCount = 0;
    private static final Object lock = new Object();

    private AsyncChannelGroupUtil() {
    }

    public static AsynchronousChannelGroup register() {
        AsynchronousChannelGroup asynchronousChannelGroup;
        synchronized (lock) {
            if (usageCount == 0) {
                group = createAsynchronousChannelGroup();
            }
            usageCount++;
            asynchronousChannelGroup = group;
        }
        return asynchronousChannelGroup;
    }

    public static void unregister() {
        synchronized (lock) {
            usageCount--;
            if (usageCount == 0) {
                group.shutdown();
                group = null;
            }
        }
    }

    private static AsynchronousChannelGroup createAsynchronousChannelGroup() {
        Thread currentThread = Thread.currentThread();
        ClassLoader original = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(AsyncIOThreadFactory.class.getClassLoader());
            int initialSize = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new AsyncIOThreadFactory());
            try {
                AsynchronousChannelGroup withCachedThreadPool = AsynchronousChannelGroup.withCachedThreadPool(executorService, initialSize);
                currentThread.setContextClassLoader(original);
                return withCachedThreadPool;
            } catch (IOException e) {
                throw new IllegalStateException(sm.getString("asyncChannelGroup.createFail"));
            }
        } catch (Throwable th) {
            currentThread.setContextClassLoader(original);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelGroupUtil$AsyncIOThreadFactory.class */
    public static class AsyncIOThreadFactory implements ThreadFactory {
        private AsyncIOThreadFactory() {
        }

        static {
            NewThreadPrivilegedAction.load();
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            return (Thread) AccessController.doPrivileged(new NewThreadPrivilegedAction(r));
        }

        /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelGroupUtil$AsyncIOThreadFactory$NewThreadPrivilegedAction.class */
        private static class NewThreadPrivilegedAction implements PrivilegedAction<Thread> {
            private static AtomicInteger count = new AtomicInteger(0);
            private final Runnable r;

            NewThreadPrivilegedAction(Runnable r) {
                this.r = r;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Thread run() {
                Thread t = new Thread(this.r);
                t.setName("WebSocketClient-AsyncIO-" + count.incrementAndGet());
                t.setContextClassLoader(getClass().getClassLoader());
                t.setDaemon(true);
                return t;
            }

            private static void load() {
            }
        }
    }
}
