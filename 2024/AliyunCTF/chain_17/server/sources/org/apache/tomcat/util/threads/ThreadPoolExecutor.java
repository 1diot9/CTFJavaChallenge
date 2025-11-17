package org.apache.tomcat.util.threads;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor.class */
public class ThreadPoolExecutor extends AbstractExecutorService {
    private final AtomicInteger ctl;
    private static final int COUNT_BITS = 29;
    private static final int COUNT_MASK = 536870911;
    private static final int RUNNING = -536870912;
    private static final int SHUTDOWN = 0;
    private static final int STOP = 536870912;
    private static final int TIDYING = 1073741824;
    private static final int TERMINATED = 1610612736;
    private final BlockingQueue<Runnable> workQueue;
    private final ReentrantLock mainLock;
    private final HashSet<Worker> workers;
    private final Condition termination;
    private int largestPoolSize;
    private long completedTaskCount;
    private final AtomicInteger submittedCount;
    private final AtomicLong lastContextStoppedTime;
    private final AtomicLong lastTimeThreadKilledItself;
    private volatile long threadRenewalDelay;
    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private static final boolean ONLY_ONE = true;
    protected static final StringManager sm = StringManager.getManager((Class<?>) ThreadPoolExecutor.class);
    private static final RejectedExecutionHandler defaultHandler = new RejectPolicy();
    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$RejectedExecutionHandler.class */
    public interface RejectedExecutionHandler {
        void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor);
    }

    private static int workerCountOf(int c) {
        return c & COUNT_MASK;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < 0;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        this.ctl.addAndGet(-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$Worker.class */
    public final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = this.thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException e) {
                }
            }
        }
    }

    private void advanceRunState(int targetState) {
        int c;
        do {
            c = this.ctl.get();
            if (runStateAtLeast(c, targetState)) {
                return;
            }
        } while (!this.ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));
    }

    final void tryTerminate() {
        while (true) {
            int c = this.ctl.get();
            if (!isRunning(c) && !runStateAtLeast(c, 1073741824)) {
                if (runStateLessThan(c, 536870912) && !this.workQueue.isEmpty()) {
                    return;
                }
                if (workerCountOf(c) != 0) {
                    interruptIdleWorkers(true);
                    return;
                }
                ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    if (this.ctl.compareAndSet(c, ctlOf(1073741824, 0))) {
                        try {
                            terminated();
                            this.ctl.set(ctlOf(TERMINATED, 0));
                            this.termination.signalAll();
                            return;
                        } catch (Throwable th) {
                            this.ctl.set(ctlOf(TERMINATED, 0));
                            this.termination.signalAll();
                            throw th;
                        }
                    }
                    mainLock.unlock();
                } finally {
                    mainLock.unlock();
                }
            } else {
                return;
            }
        }
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                security.checkAccess(w.thread);
            }
        }
    }

    private void interruptWorkers() {
        Iterator<Worker> it = this.workers.iterator();
        while (it.hasNext()) {
            Worker w = it.next();
            w.interruptIfStarted();
        }
    }

    private void interruptIdleWorkers(boolean onlyOne) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                        w.unlock();
                    } catch (SecurityException e) {
                        w.unlock();
                    } catch (Throwable th) {
                        w.unlock();
                        throw th;
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    final void reject(Runnable command) {
        this.handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = this.workQueue;
        ArrayList<Runnable> taskList = new ArrayList<>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : (Runnable[]) q.toArray(new Runnable[0])) {
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    /* JADX WARN: Finally extract failed */
    private boolean addWorker(Runnable firstTask, boolean core) {
        int c = this.ctl.get();
        while (true) {
            if (runStateAtLeast(c, 0) && (runStateAtLeast(c, 536870912) || firstTask != null || this.workQueue.isEmpty())) {
                return false;
            }
            do {
                if (workerCountOf(c) >= ((core ? this.corePoolSize : this.maximumPoolSize) & COUNT_MASK)) {
                    return false;
                }
                if (!compareAndIncrementWorkerCount(c)) {
                    c = this.ctl.get();
                } else {
                    boolean workerStarted = false;
                    boolean workerAdded = false;
                    Worker w = null;
                    try {
                        w = new Worker(firstTask);
                        Thread t = w.thread;
                        if (t != null) {
                            ReentrantLock mainLock = this.mainLock;
                            mainLock.lock();
                            try {
                                int c2 = this.ctl.get();
                                if (isRunning(c2) || (runStateLessThan(c2, 536870912) && firstTask == null)) {
                                    if (t.getState() != Thread.State.NEW) {
                                        throw new IllegalThreadStateException();
                                    }
                                    this.workers.add(w);
                                    workerAdded = true;
                                    int s = this.workers.size();
                                    if (s > this.largestPoolSize) {
                                        this.largestPoolSize = s;
                                    }
                                }
                                mainLock.unlock();
                                if (workerAdded) {
                                    t.start();
                                    workerStarted = true;
                                }
                            } catch (Throwable th) {
                                mainLock.unlock();
                                throw th;
                            }
                        }
                        if (!workerStarted) {
                            addWorkerFailed(w);
                        }
                        return workerStarted;
                    } catch (Throwable th2) {
                        if (0 == 0) {
                            addWorkerFailed(w);
                        }
                        throw th2;
                    }
                }
            } while (!runStateAtLeast(c, 0));
        }
    }

    private void addWorkerFailed(Worker w) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        if (w != null) {
            try {
                this.workers.remove(w);
            } finally {
                mainLock.unlock();
            }
        }
        decrementWorkerCount();
        tryTerminate();
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) {
            decrementWorkerCount();
        }
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
            mainLock.unlock();
            tryTerminate();
            int c = this.ctl.get();
            if (runStateLessThan(c, 536870912)) {
                if (!completedAbruptly) {
                    int min = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
                    if (min == 0 && !this.workQueue.isEmpty()) {
                        min = 1;
                    }
                    if (workerCountOf(c) >= min && this.workQueue.isEmpty()) {
                        return;
                    }
                }
                addWorker(null, false);
            }
        } catch (Throwable th) {
            mainLock.unlock();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x002c, code lost:            return null;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0027, code lost:            decrementWorkerCount();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.Runnable getTask() {
        /*
            r5 = this;
            r0 = 0
            r6 = r0
        L2:
            r0 = r5
            java.util.concurrent.atomic.AtomicInteger r0 = r0.ctl
            int r0 = r0.get()
            r7 = r0
            r0 = r7
            r1 = 0
            boolean r0 = runStateAtLeast(r0, r1)
            if (r0 == 0) goto L2d
            r0 = r7
            r1 = 536870912(0x20000000, float:1.0842022E-19)
            boolean r0 = runStateAtLeast(r0, r1)
            if (r0 != 0) goto L27
            r0 = r5
            java.util.concurrent.BlockingQueue<java.lang.Runnable> r0 = r0.workQueue
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L2d
        L27:
            r0 = r5
            r0.decrementWorkerCount()
            r0 = 0
            return r0
        L2d:
            r0 = r7
            int r0 = workerCountOf(r0)
            r8 = r0
            r0 = r5
            boolean r0 = r0.allowCoreThreadTimeOut
            if (r0 != 0) goto L41
            r0 = r8
            r1 = r5
            int r1 = r1.corePoolSize
            if (r0 <= r1) goto L45
        L41:
            r0 = 1
            goto L46
        L45:
            r0 = 0
        L46:
            r9 = r0
            r0 = r8
            r1 = r5
            int r1 = r1.maximumPoolSize
            if (r0 > r1) goto L59
            r0 = r9
            if (r0 == 0) goto L74
            r0 = r6
            if (r0 == 0) goto L74
        L59:
            r0 = r8
            r1 = 1
            if (r0 > r1) goto L6a
            r0 = r5
            java.util.concurrent.BlockingQueue<java.lang.Runnable> r0 = r0.workQueue
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L74
        L6a:
            r0 = r5
            r1 = r7
            boolean r0 = r0.compareAndDecrementWorkerCount(r1)
            if (r0 == 0) goto L2
            r0 = 0
            return r0
        L74:
            r0 = r9
            if (r0 == 0) goto L8f
            r0 = r5
            java.util.concurrent.BlockingQueue<java.lang.Runnable> r0 = r0.workQueue     // Catch: java.lang.InterruptedException -> Laa
            r1 = r5
            long r1 = r1.keepAliveTime     // Catch: java.lang.InterruptedException -> Laa
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch: java.lang.InterruptedException -> Laa
            java.lang.Object r0 = r0.poll(r1, r2)     // Catch: java.lang.InterruptedException -> Laa
            java.lang.Runnable r0 = (java.lang.Runnable) r0     // Catch: java.lang.InterruptedException -> Laa
            goto L9b
        L8f:
            r0 = r5
            java.util.concurrent.BlockingQueue<java.lang.Runnable> r0 = r0.workQueue     // Catch: java.lang.InterruptedException -> Laa
            java.lang.Object r0 = r0.take()     // Catch: java.lang.InterruptedException -> Laa
            java.lang.Runnable r0 = (java.lang.Runnable) r0     // Catch: java.lang.InterruptedException -> Laa
        L9b:
            r10 = r0
            r0 = r10
            if (r0 == 0) goto La5
            r0 = r10
            return r0
        La5:
            r0 = 1
            r6 = r0
            goto Lae
        Laa:
            r10 = move-exception
            r0 = 0
            r6 = r0
        Lae:
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.threads.ThreadPoolExecutor.getTask():java.lang.Runnable");
    }

    /* JADX WARN: Finally extract failed */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        boolean completedAbruptly = true;
        while (true) {
            if (task == null) {
                try {
                    Runnable task2 = getTask();
                    task = task2;
                    if (task2 == null) {
                        completedAbruptly = false;
                        return;
                    }
                } finally {
                    processWorkerExit(w, completedAbruptly);
                }
            }
            w.lock();
            if ((runStateAtLeast(this.ctl.get(), 536870912) || (Thread.interrupted() && runStateAtLeast(this.ctl.get(), 536870912))) && !wt.isInterrupted()) {
                wt.interrupt();
            }
            try {
                beforeExecute(wt, task);
                try {
                    task.run();
                    afterExecute(task, null);
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                } finally {
                }
            } catch (Throwable th) {
                w.completedTasks++;
                w.unlock();
                throw th;
            }
        }
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this.ctl = new AtomicInteger(ctlOf(RUNNING, 0));
        this.mainLock = new ReentrantLock();
        this.workers = new HashSet<>();
        this.termination = this.mainLock.newCondition();
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
        prestartAllCoreThreads();
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        this.submittedCount.incrementAndGet();
        try {
            executeInternal(command);
        } catch (RejectedExecutionException rx) {
            if (getQueue() instanceof TaskQueue) {
                TaskQueue queue = (TaskQueue) getQueue();
                if (!queue.force(command)) {
                    this.submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(sm.getString("threadPoolExecutor.queueFull"));
                }
                return;
            }
            this.submittedCount.decrementAndGet();
            throw rx;
        }
    }

    private void executeInternal(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        int c = this.ctl.get();
        if (workerCountOf(c) < this.corePoolSize) {
            if (addWorker(command, true)) {
                return;
            } else {
                c = this.ctl.get();
            }
        }
        if (isRunning(c) && this.workQueue.offer(command)) {
            int recheck = this.ctl.get();
            if (!isRunning(recheck) && remove(command)) {
                reject(command);
                return;
            } else {
                if (workerCountOf(recheck) == 0) {
                    addWorker(null, false);
                    return;
                }
                return;
            }
        }
        if (!addWorker(command, false)) {
            reject(command);
        }
    }

    @Override // java.util.concurrent.ExecutorService
    public void shutdown() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(0);
            interruptIdleWorkers();
            onShutdown();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    @Override // java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(536870912);
            interruptWorkers();
            List<Runnable> tasks = drainQueue();
            tryTerminate();
            return tasks;
        } finally {
            mainLock.unlock();
        }
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return runStateAtLeast(this.ctl.get(), 0);
    }

    boolean isStopped() {
        return runStateAtLeast(this.ctl.get(), 536870912);
    }

    public boolean isTerminating() {
        int c = this.ctl.get();
        return runStateAtLeast(c, 0) && runStateLessThan(c, TERMINATED);
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        return runStateAtLeast(this.ctl.get(), TERMINATED);
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        while (runStateLessThan(this.ctl.get(), TERMINATED)) {
            try {
                if (nanos <= 0) {
                    return false;
                }
                nanos = this.termination.awaitNanos(nanos);
            } finally {
                mainLock.unlock();
            }
        }
        mainLock.unlock();
        return true;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.handler;
    }

    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0 || this.maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException();
        }
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(this.ctl.get()) > corePoolSize) {
            interruptIdleWorkers();
            return;
        }
        if (delta > 0) {
            int k = Math.min(delta, this.workQueue.size());
            do {
                int i = k;
                k--;
                if (i <= 0 || !addWorker(null, true)) {
                    return;
                }
            } while (!this.workQueue.isEmpty());
        }
    }

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public boolean prestartCoreThread() {
        return workerCountOf(this.ctl.get()) < this.corePoolSize && addWorker(null, true);
    }

    void ensurePrestart() {
        int wc = workerCountOf(this.ctl.get());
        if (wc < this.corePoolSize) {
            addWorker(null, true);
        } else if (wc == 0) {
            addWorker(null, false);
        }
    }

    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true)) {
            n++;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return this.allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && this.keepAliveTime <= 0) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        if (value != this.allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = value;
            if (value) {
                interruptIdleWorkers();
            }
        }
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(this.ctl.get()) > maximumPoolSize) {
            interruptIdleWorkers();
        }
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        if (time == 0 && allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException(sm.getString("threadPoolExecutor.invalidKeepAlive"));
        }
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0) {
            interruptIdleWorkers();
        }
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }

    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
    }

    public BlockingQueue<Runnable> getQueue() {
        return this.workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = this.workQueue.remove(task);
        tryTerminate();
        return removed;
    }

    public void purge() {
        BlockingQueue<Runnable> q = this.workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if ((r instanceof Future) && ((Future) r).isCancelled()) {
                    it.remove();
                }
            }
        } catch (ConcurrentModificationException e) {
            for (Object r2 : q.toArray()) {
                if ((r2 instanceof Future) && ((Future) r2).isCancelled()) {
                    q.remove(r2);
                }
            }
        }
        tryTerminate();
    }

    public void contextStopping() {
        this.lastContextStoppedTime.set(System.currentTimeMillis());
        int savedCorePoolSize = getCorePoolSize();
        setCorePoolSize(0);
        setCorePoolSize(savedCorePoolSize);
    }

    public int getPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return runStateAtLeast(this.ctl.get(), 1073741824) ? 0 : this.workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPoolSizeNoLock() {
        if (runStateAtLeast(this.ctl.get(), 1073741824)) {
            return 0;
        }
        return this.workers.size();
    }

    public int getActiveCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                if (w.isLocked()) {
                    n++;
                }
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return this.largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    public long getTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                n += w.completedTasks;
                if (w.isLocked()) {
                    n++;
                }
            }
            long size = n + this.workQueue.size();
            mainLock.unlock();
            return size;
        } catch (Throwable th) {
            mainLock.unlock();
            throw th;
        }
    }

    public long getCompletedTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                n += w.completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getSubmittedCount() {
        return this.submittedCount.get();
    }

    public String toString() {
        String str;
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long ncompleted = this.completedTaskCount;
            int nactive = 0;
            int nworkers = this.workers.size();
            Iterator<Worker> it = this.workers.iterator();
            while (it.hasNext()) {
                Worker w = it.next();
                ncompleted += w.completedTasks;
                if (w.isLocked()) {
                    nactive++;
                }
            }
            int c = this.ctl.get();
            if (isRunning(c)) {
                str = "Running";
            } else {
                str = runStateAtLeast(c, TERMINATED) ? "Terminated" : "Shutting down";
            }
            String runState = str;
            return super.toString() + "[" + runState + ", pool size = " + nworkers + ", active threads = " + nactive + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + ncompleted + "]";
        } finally {
            mainLock.unlock();
        }
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Runnable r, Throwable t) {
        if (!(t instanceof StopPooledThreadException)) {
            this.submittedCount.decrementAndGet();
        }
        if (t == null) {
            stopCurrentThreadIfNeeded();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopCurrentThreadIfNeeded() {
        if (currentThreadShouldBeStopped()) {
            long lastTime = this.lastTimeThreadKilledItself.longValue();
            if (lastTime + this.threadRenewalDelay < System.currentTimeMillis() && this.lastTimeThreadKilledItself.compareAndSet(lastTime, System.currentTimeMillis() + 1)) {
                String msg = sm.getString("threadPoolExecutor.threadStoppedToAvoidPotentialLeak", Thread.currentThread().getName());
                throw new StopPooledThreadException(msg);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean currentThreadShouldBeStopped() {
        Thread currentThread = Thread.currentThread();
        if (this.threadRenewalDelay >= 0 && (currentThread instanceof TaskThread)) {
            TaskThread currentTaskThread = (TaskThread) currentThread;
            if (currentTaskThread.getCreationTime() < this.lastContextStoppedTime.longValue()) {
                return true;
            }
            return false;
        }
        return false;
    }

    protected void terminated() {
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$CallerRunsPolicy.class */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        @Override // org.apache.tomcat.util.threads.ThreadPoolExecutor.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$AbortPolicy.class */
    public static class AbortPolicy implements RejectedExecutionHandler {
        @Override // org.apache.tomcat.util.threads.ThreadPoolExecutor.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException(ThreadPoolExecutor.sm.getString("threadPoolExecutor.taskRejected", r.toString(), e.toString()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$DiscardPolicy.class */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        @Override // org.apache.tomcat.util.threads.ThreadPoolExecutor.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$DiscardOldestPolicy.class */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        @Override // org.apache.tomcat.util.threads.ThreadPoolExecutor.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ThreadPoolExecutor$RejectPolicy.class */
    private static class RejectPolicy implements RejectedExecutionHandler {
        private RejectPolicy() {
        }

        @Override // org.apache.tomcat.util.threads.ThreadPoolExecutor.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RejectedExecutionException();
        }
    }
}
