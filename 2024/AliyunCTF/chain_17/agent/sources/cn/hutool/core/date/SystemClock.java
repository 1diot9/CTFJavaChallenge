package cn.hutool.core.date;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/SystemClock.class */
public class SystemClock {
    private final long period;
    private volatile long now = System.currentTimeMillis();

    public SystemClock(long period) {
        this.period = period;
        scheduleClockUpdating();
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> {
            this.now = System.currentTimeMillis();
        }, this.period, this.period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/SystemClock$InstanceHolder.class */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);

        private InstanceHolder() {
        }
    }

    public static long now() {
        return InstanceHolder.INSTANCE.currentTimeMillis();
    }

    public static String nowDate() {
        return new Timestamp(InstanceHolder.INSTANCE.currentTimeMillis()).toString();
    }
}
