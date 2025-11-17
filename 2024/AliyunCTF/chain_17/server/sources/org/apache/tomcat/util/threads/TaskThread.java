package org.apache.tomcat.util.threads;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/TaskThread.class */
public class TaskThread extends Thread {
    private static final Log log = LogFactory.getLog((Class<?>) TaskThread.class);
    private final long creationTime;

    public TaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, new WrappingRunnable(target), name);
        this.creationTime = System.currentTimeMillis();
    }

    public TaskThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, new WrappingRunnable(target), name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }

    public final long getCreationTime() {
        return this.creationTime;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/TaskThread$WrappingRunnable.class */
    private static class WrappingRunnable implements Runnable {
        private Runnable wrappedRunnable;

        WrappingRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.wrappedRunnable.run();
            } catch (StopPooledThreadException exc) {
                TaskThread.log.debug("Thread exiting on purpose", exc);
            }
        }
    }
}
