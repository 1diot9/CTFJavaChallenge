package cn.hutool.core.thread;

import java.util.concurrent.Semaphore;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/SemaphoreRunnable.class */
public class SemaphoreRunnable implements Runnable {
    private final Runnable runnable;
    private final Semaphore semaphore;

    public SemaphoreRunnable(Runnable runnable, Semaphore semaphore) {
        this.runnable = runnable;
        this.semaphore = semaphore;
    }

    public Semaphore getSemaphore() {
        return this.semaphore;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (null != this.semaphore) {
            try {
                this.semaphore.acquire();
                try {
                    this.runnable.run();
                    this.semaphore.release();
                } catch (Throwable th) {
                    this.semaphore.release();
                    throw th;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
