package ch.qos.logback.core;

import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.util.InterruptUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/AsyncAppenderBase.class */
public class AsyncAppenderBase<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
    BlockingQueue<E> blockingQueue;
    public static final int DEFAULT_QUEUE_SIZE = 256;
    static final int UNDEFINED = -1;
    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<>();
    int queueSize = 256;
    int appenderCount = 0;
    int discardingThreshold = -1;
    boolean neverBlock = false;
    AsyncAppenderBase<E>.Worker worker = new Worker();
    int maxFlushTime = 1000;

    protected boolean isDiscardable(E eventObject) {
        return false;
    }

    protected void preprocess(E eventObject) {
    }

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (isStarted()) {
            return;
        }
        if (this.appenderCount == 0) {
            addError("No attached appenders found.");
            return;
        }
        if (this.queueSize < 1) {
            addError("Invalid queue size [" + this.queueSize + "]");
            return;
        }
        this.blockingQueue = new ArrayBlockingQueue(this.queueSize);
        if (this.discardingThreshold == -1) {
            this.discardingThreshold = this.queueSize / 5;
        }
        addInfo("Setting discardingThreshold to " + this.discardingThreshold);
        this.worker.setDaemon(true);
        this.worker.setName("AsyncAppender-Worker-" + getName());
        super.start();
        this.worker.start();
    }

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        super.stop();
        this.worker.interrupt();
        InterruptUtil interruptUtil = new InterruptUtil(this.context);
        try {
            try {
                interruptUtil.maskInterruptFlag();
                this.worker.join(this.maxFlushTime);
                if (this.worker.isAlive()) {
                    addWarn("Max queue flush timeout (" + this.maxFlushTime + " ms) exceeded. Approximately " + this.blockingQueue.size() + " queued events were possibly discarded.");
                } else {
                    addInfo("Queue flush finished successfully within timeout.");
                }
                interruptUtil.unmaskInterruptFlag();
            } catch (InterruptedException e) {
                int remaining = this.blockingQueue.size();
                addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", e);
                interruptUtil.unmaskInterruptFlag();
            }
        } catch (Throwable th) {
            interruptUtil.unmaskInterruptFlag();
            throw th;
        }
    }

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase
    protected void append(E eventObject) {
        if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) {
            return;
        }
        preprocess(eventObject);
        put(eventObject);
    }

    private boolean isQueueBelowDiscardingThreshold() {
        return this.blockingQueue.remainingCapacity() < this.discardingThreshold;
    }

    private void put(E eventObject) {
        if (this.neverBlock) {
            this.blockingQueue.offer(eventObject);
        } else {
            putUninterruptibly(eventObject);
        }
    }

    private void putUninterruptibly(E eventObject) {
        boolean interrupted = false;
        while (true) {
            try {
                this.blockingQueue.put(eventObject);
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            } catch (Throwable th) {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return this.discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return this.maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    public int getNumberOfElementsInQueue() {
        return this.blockingQueue.size();
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }

    public boolean isNeverBlock() {
        return this.neverBlock;
    }

    public int getRemainingCapacity() {
        return this.blockingQueue.remainingCapacity();
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public void addAppender(Appender<E> newAppender) {
        if (this.appenderCount == 0) {
            this.appenderCount++;
            addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
            this.aai.addAppender(newAppender);
        } else {
            addWarn("One and only one appender may be attached to AsyncAppender.");
            addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.aai.iteratorForAppenders();
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public Appender<E> getAppender(String name) {
        return this.aai.getAppender(name);
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean isAttached(Appender<E> eAppender) {
        return this.aai.isAttached(eAppender);
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public void detachAndStopAllAppenders() {
        this.aai.detachAndStopAllAppenders();
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean detachAppender(Appender<E> eAppender) {
        return this.aai.detachAppender(eAppender);
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean detachAppender(String name) {
        return this.aai.detachAppender(name);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/AsyncAppenderBase$Worker.class */
    class Worker extends Thread {
        Worker() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            AsyncAppenderBase asyncAppenderBase = AsyncAppenderBase.this;
            AppenderAttachableImpl<E> appenderAttachableImpl = asyncAppenderBase.aai;
            while (asyncAppenderBase.isStarted()) {
                try {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(asyncAppenderBase.blockingQueue.take());
                    asyncAppenderBase.blockingQueue.drainTo(arrayList);
                    Iterator<E> it = arrayList.iterator();
                    while (it.hasNext()) {
                        appenderAttachableImpl.appendLoopOnAppenders(it.next());
                    }
                } catch (InterruptedException e) {
                }
            }
            AsyncAppenderBase.this.addInfo("Worker thread will flush remaining events before exiting. ");
            for (Object obj : asyncAppenderBase.blockingQueue) {
                appenderAttachableImpl.appendLoopOnAppenders(obj);
                asyncAppenderBase.blockingQueue.remove(obj);
            }
            appenderAttachableImpl.detachAndStopAllAppenders();
        }
    }
}
