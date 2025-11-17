package cn.hutool.core.thread;

import cn.hutool.core.exceptions.UtilException;
import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/SyncFinisher.class */
public class SyncFinisher implements Closeable {
    private final int threadSize;
    private ExecutorService executorService;
    private boolean isBeginAtSameTime;
    private CountDownLatch endLatch;
    private final CountDownLatch beginLatch = new CountDownLatch(1);
    private final Set<Worker> workers = new LinkedHashSet();

    public SyncFinisher(int threadSize) {
        this.threadSize = threadSize;
    }

    public SyncFinisher setBeginAtSameTime(boolean isBeginAtSameTime) {
        this.isBeginAtSameTime = isBeginAtSameTime;
        return this;
    }

    public SyncFinisher addRepeatWorker(final Runnable runnable) {
        for (int i = 0; i < this.threadSize; i++) {
            addWorker(new Worker() { // from class: cn.hutool.core.thread.SyncFinisher.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super();
                }

                @Override // cn.hutool.core.thread.SyncFinisher.Worker
                public void work() {
                    runnable.run();
                }
            });
        }
        return this;
    }

    public SyncFinisher addWorker(final Runnable runnable) {
        return addWorker(new Worker() { // from class: cn.hutool.core.thread.SyncFinisher.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // cn.hutool.core.thread.SyncFinisher.Worker
            public void work() {
                runnable.run();
            }
        });
    }

    public synchronized SyncFinisher addWorker(Worker worker) {
        this.workers.add(worker);
        return this;
    }

    public void start() {
        start(true);
    }

    public void start(boolean sync) {
        this.endLatch = new CountDownLatch(this.workers.size());
        if (null == this.executorService || this.executorService.isShutdown()) {
            this.executorService = ThreadUtil.newExecutor(this.threadSize);
        }
        for (Worker worker : this.workers) {
            this.executorService.submit(worker);
        }
        this.beginLatch.countDown();
        if (sync) {
            try {
                this.endLatch.await();
            } catch (InterruptedException e) {
                throw new UtilException(e);
            }
        }
    }

    public void stop() {
        if (null != this.executorService) {
            this.executorService.shutdown();
            this.executorService = null;
        }
        clearWorker();
    }

    public void stopNow() {
        if (null != this.executorService) {
            this.executorService.shutdownNow();
            this.executorService = null;
        }
        clearWorker();
    }

    public void clearWorker() {
        this.workers.clear();
    }

    public long count() {
        return this.endLatch.getCount();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        stop();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/SyncFinisher$Worker.class */
    public abstract class Worker implements Runnable {
        public abstract void work();

        public Worker() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SyncFinisher.this.isBeginAtSameTime) {
                try {
                    SyncFinisher.this.beginLatch.await();
                } catch (InterruptedException e) {
                    throw new UtilException(e);
                }
            }
            try {
                work();
            } finally {
                SyncFinisher.this.endLatch.countDown();
            }
        }
    }
}
