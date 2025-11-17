package cn.hutool.core.thread;

import cn.hutool.core.date.TimeInterval;
import java.io.Closeable;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/ConcurrencyTester.class */
public class ConcurrencyTester implements Closeable {
    private final SyncFinisher sf;
    private final TimeInterval timeInterval = new TimeInterval();
    private long interval;

    public ConcurrencyTester(int threadSize) {
        this.sf = new SyncFinisher(threadSize);
    }

    public ConcurrencyTester test(Runnable runnable) {
        this.sf.clearWorker();
        this.timeInterval.start();
        this.sf.addRepeatWorker(runnable).setBeginAtSameTime(true).start();
        this.interval = this.timeInterval.interval();
        return this;
    }

    public ConcurrencyTester reset() {
        this.sf.clearWorker();
        this.timeInterval.restart();
        return this;
    }

    public long getInterval() {
        return this.interval;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.sf.close();
    }
}
