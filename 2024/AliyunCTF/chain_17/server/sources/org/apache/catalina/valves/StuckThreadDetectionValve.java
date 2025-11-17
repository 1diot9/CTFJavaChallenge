package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/StuckThreadDetectionValve.class */
public class StuckThreadDetectionValve extends ValveBase {
    private static final Log log = LogFactory.getLog((Class<?>) StuckThreadDetectionValve.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);
    private final AtomicInteger stuckCount;
    private AtomicLong interruptedThreadsCount;
    private int threshold;
    private int interruptThreadThreshold;
    private final Map<Long, MonitoredThread> activeThreads;
    private final Queue<CompletedStuckThread> completedStuckThreadsQueue;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/StuckThreadDetectionValve$MonitoredThreadState.class */
    public enum MonitoredThreadState {
        RUNNING,
        STUCK,
        DONE
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getInterruptThreadThreshold() {
        return this.interruptThreadThreshold;
    }

    public void setInterruptThreadThreshold(int interruptThreadThreshold) {
        this.interruptThreadThreshold = interruptThreadThreshold;
    }

    public StuckThreadDetectionValve() {
        super(true);
        this.stuckCount = new AtomicInteger(0);
        this.interruptedThreadsCount = new AtomicLong();
        this.threshold = 600;
        this.activeThreads = new ConcurrentHashMap();
        this.completedStuckThreadsQueue = new ConcurrentLinkedQueue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (log.isDebugEnabled()) {
            log.debug("Monitoring stuck threads with threshold = " + this.threshold + " sec");
        }
    }

    private void notifyStuckThreadDetected(MonitoredThread monitoredThread, long activeTime, int numStuckThreads) {
        if (log.isWarnEnabled()) {
            String msg = sm.getString("stuckThreadDetectionValve.notifyStuckThreadDetected", monitoredThread.getThread().getName(), Long.valueOf(activeTime), monitoredThread.getStartTime(), Integer.valueOf(numStuckThreads), monitoredThread.getRequestUri(), Integer.valueOf(this.threshold), String.valueOf(monitoredThread.getThread().getId()));
            Throwable th = new Throwable();
            th.setStackTrace(monitoredThread.getThread().getStackTrace());
            log.warn(msg, th);
        }
    }

    private void notifyStuckThreadCompleted(CompletedStuckThread thread, int numStuckThreads) {
        if (log.isWarnEnabled()) {
            String msg = sm.getString("stuckThreadDetectionValve.notifyStuckThreadCompleted", thread.getName(), Long.valueOf(thread.getTotalActiveTime()), Integer.valueOf(numStuckThreads), String.valueOf(thread.getId()));
            log.warn(msg);
        }
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.threshold <= 0) {
            getNext().invoke(request, response);
            return;
        }
        Thread currentThread = Thread.currentThread();
        Long key = Long.valueOf(currentThread.getId());
        StringBuffer requestUrl = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestUrl.append('?');
            requestUrl.append(request.getQueryString());
        }
        MonitoredThread monitoredThread = new MonitoredThread(currentThread, requestUrl.toString(), this.interruptThreadThreshold > 0);
        this.activeThreads.put(key, monitoredThread);
        try {
            getNext().invoke(request, response);
            this.activeThreads.remove(key);
            if (monitoredThread.markAsDone() == MonitoredThreadState.STUCK) {
                if (monitoredThread.wasInterrupted()) {
                    this.interruptedThreadsCount.incrementAndGet();
                }
                this.completedStuckThreadsQueue.add(new CompletedStuckThread(monitoredThread.getThread(), monitoredThread.getActiveTimeInMillis()));
            }
        } catch (Throwable th) {
            this.activeThreads.remove(key);
            if (monitoredThread.markAsDone() == MonitoredThreadState.STUCK) {
                if (monitoredThread.wasInterrupted()) {
                    this.interruptedThreadsCount.incrementAndGet();
                }
                this.completedStuckThreadsQueue.add(new CompletedStuckThread(monitoredThread.getThread(), monitoredThread.getActiveTimeInMillis()));
            }
            throw th;
        }
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Valve
    public void backgroundProcess() {
        super.backgroundProcess();
        long thresholdInMillis = this.threshold * 1000;
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            long activeTime = monitoredThread.getActiveTimeInMillis();
            if (activeTime >= thresholdInMillis && monitoredThread.markAsStuckIfStillRunning()) {
                int numStuckThreads = this.stuckCount.incrementAndGet();
                notifyStuckThreadDetected(monitoredThread, activeTime, numStuckThreads);
            }
            if (this.interruptThreadThreshold > 0 && activeTime >= this.interruptThreadThreshold * 1000) {
                monitoredThread.interruptIfStuck(this.interruptThreadThreshold);
            }
        }
        CompletedStuckThread poll = this.completedStuckThreadsQueue.poll();
        while (true) {
            CompletedStuckThread completedStuckThread = poll;
            if (completedStuckThread != null) {
                int numStuckThreads2 = this.stuckCount.decrementAndGet();
                notifyStuckThreadCompleted(completedStuckThread, numStuckThreads2);
                poll = this.completedStuckThreadsQueue.poll();
            } else {
                return;
            }
        }
    }

    public int getStuckThreadCount() {
        return this.stuckCount.get();
    }

    public long[] getStuckThreadIds() {
        List<Long> idList = new ArrayList<>();
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            if (monitoredThread.isMarkedAsStuck()) {
                idList.add(Long.valueOf(monitoredThread.getThread().getId()));
            }
        }
        long[] result = new long[idList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = idList.get(i).longValue();
        }
        return result;
    }

    public String[] getStuckThreadNames() {
        List<String> nameList = new ArrayList<>();
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            if (monitoredThread.isMarkedAsStuck()) {
                nameList.add(monitoredThread.getThread().getName());
            }
        }
        return (String[]) nameList.toArray(new String[0]);
    }

    public long getInterruptedThreadsCount() {
        return this.interruptedThreadsCount.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/StuckThreadDetectionValve$MonitoredThread.class */
    public static class MonitoredThread {
        private final Thread thread;
        private final String requestUri;
        private final Semaphore interruptionSemaphore;
        private boolean interrupted;
        private final AtomicInteger state = new AtomicInteger(MonitoredThreadState.RUNNING.ordinal());
        private final long start = System.currentTimeMillis();

        MonitoredThread(Thread thread, String requestUri, boolean interruptible) {
            this.thread = thread;
            this.requestUri = requestUri;
            if (interruptible) {
                this.interruptionSemaphore = new Semaphore(1);
            } else {
                this.interruptionSemaphore = null;
            }
        }

        public Thread getThread() {
            return this.thread;
        }

        public String getRequestUri() {
            return this.requestUri;
        }

        public long getActiveTimeInMillis() {
            return System.currentTimeMillis() - this.start;
        }

        public Date getStartTime() {
            return new Date(this.start);
        }

        public boolean markAsStuckIfStillRunning() {
            return this.state.compareAndSet(MonitoredThreadState.RUNNING.ordinal(), MonitoredThreadState.STUCK.ordinal());
        }

        public MonitoredThreadState markAsDone() {
            int val = this.state.getAndSet(MonitoredThreadState.DONE.ordinal());
            MonitoredThreadState threadState = MonitoredThreadState.values()[val];
            if (threadState == MonitoredThreadState.STUCK && this.interruptionSemaphore != null) {
                try {
                    this.interruptionSemaphore.acquire();
                } catch (InterruptedException e) {
                    StuckThreadDetectionValve.log.debug("thread interrupted after the request is finished, ignoring", e);
                }
            }
            return threadState;
        }

        boolean isMarkedAsStuck() {
            return this.state.get() == MonitoredThreadState.STUCK.ordinal();
        }

        public boolean interruptIfStuck(long interruptThreadThreshold) {
            if (!isMarkedAsStuck() || this.interruptionSemaphore == null || !this.interruptionSemaphore.tryAcquire()) {
                return false;
            }
            try {
                if (StuckThreadDetectionValve.log.isWarnEnabled()) {
                    String msg = StuckThreadDetectionValve.sm.getString("stuckThreadDetectionValve.notifyStuckThreadInterrupted", getThread().getName(), Long.valueOf(getActiveTimeInMillis()), getStartTime(), getRequestUri(), Long.valueOf(interruptThreadThreshold), String.valueOf(getThread().getId()));
                    Throwable th = new Throwable();
                    th.setStackTrace(getThread().getStackTrace());
                    StuckThreadDetectionValve.log.warn(msg, th);
                }
                this.thread.interrupt();
                this.interrupted = true;
                this.interruptionSemaphore.release();
                return true;
            } catch (Throwable th2) {
                this.interrupted = true;
                this.interruptionSemaphore.release();
                throw th2;
            }
        }

        public boolean wasInterrupted() {
            return this.interrupted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/StuckThreadDetectionValve$CompletedStuckThread.class */
    public static class CompletedStuckThread {
        private final String threadName;
        private final long threadId;
        private final long totalActiveTime;

        CompletedStuckThread(Thread thread, long totalActiveTime) {
            this.threadName = thread.getName();
            this.threadId = thread.getId();
            this.totalActiveTime = totalActiveTime;
        }

        public String getName() {
            return this.threadName;
        }

        public long getId() {
            return this.threadId;
        }

        public long getTotalActiveTime() {
            return this.totalActiveTime;
        }
    }
}
