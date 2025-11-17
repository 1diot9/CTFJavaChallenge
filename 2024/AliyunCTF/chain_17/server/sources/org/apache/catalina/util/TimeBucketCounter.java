package org.apache.catalina.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/TimeBucketCounter.class */
public class TimeBucketCounter {
    private static final Log log = LogFactory.getLog((Class<?>) TimeBucketCounter.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) TimeBucketCounter.class);
    private final ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
    private final int numBits;
    private final double ratio;
    private ScheduledFuture<?> maintenanceFuture;
    private ScheduledFuture<?> monitorFuture;
    private final ScheduledExecutorService executorService;
    private final long sleeptime;

    /* JADX WARN: Incorrect condition in loop: B:3:0x002a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public TimeBucketCounter(int r10, java.util.concurrent.ScheduledExecutorService r11) {
        /*
            r9 = this;
            r0 = r9
            r0.<init>()
            r0 = r9
            java.util.concurrent.ConcurrentHashMap r1 = new java.util.concurrent.ConcurrentHashMap
            r2 = r1
            r2.<init>()
            r0.map = r1
            r0 = r9
            r1 = r11
            r0.executorService = r1
            r0 = r10
            r1 = 1000(0x3e8, float:1.401E-42)
            int r0 = r0 * r1
            r12 = r0
            r0 = 0
            r13 = r0
            r0 = r12
            int r0 = nextPowerOf2(r0)
            r14 = r0
            r0 = r14
            r15 = r0
        L27:
            r0 = r15
            r1 = 1
            if (r0 <= r1) goto L3a
            r0 = r14
            int r13 = r13 + 1
            r1 = r13
            int r0 = r0 >> r1
            r15 = r0
            goto L27
        L3a:
            r0 = r9
            r1 = r13
            r0.numBits = r1
            r0 = r9
            r1 = r12
            double r1 = ratioToPowerOf2(r1)
            r0.ratio = r1
            r0 = r12
            r1 = 60000(0xea60, float:8.4078E-41)
            if (r0 < r1) goto L53
            r0 = 6
            goto L54
        L53:
            r0 = 3
        L54:
            r16 = r0
            r0 = r9
            r1 = r12
            r2 = r16
            int r1 = r1 / r2
            long r1 = (long) r1
            r0.sleeptime = r1
            r0 = r9
            long r0 = r0.sleeptime
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L81
            r0 = r9
            r1 = r11
            org.apache.catalina.util.TimeBucketCounter$MaintenanceMonitor r2 = new org.apache.catalina.util.TimeBucketCounter$MaintenanceMonitor
            r3 = r2
            r4 = r9
            r3.<init>()
            r3 = 0
            r4 = 60
            java.util.concurrent.TimeUnit r5 = java.util.concurrent.TimeUnit.SECONDS
            java.util.concurrent.ScheduledFuture r1 = r1.scheduleWithFixedDelay(r2, r3, r4, r5)
            r0.monitorFuture = r1
        L81:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.util.TimeBucketCounter.<init>(int, java.util.concurrent.ScheduledExecutorService):void");
    }

    public final int increment(String identifier) {
        String key = getCurrentBucketPrefix() + "-" + identifier;
        AtomicInteger ai = this.map.computeIfAbsent(key, v -> {
            return new AtomicInteger();
        });
        return ai.incrementAndGet();
    }

    public final int getCurrentBucketPrefix() {
        return (int) (System.currentTimeMillis() >> this.numBits);
    }

    public int getNumBits() {
        return this.numBits;
    }

    public int getActualDuration() {
        return (int) Math.pow(2.0d, getNumBits());
    }

    public double getRatio() {
        return this.ratio;
    }

    static double ratioToPowerOf2(int value) {
        double nextPO2 = nextPowerOf2(value);
        return Math.round((1000.0d * nextPO2) / value) / 1000.0d;
    }

    static int nextPowerOf2(int value) {
        int valueOfHighestBit = Integer.highestOneBit(value);
        if (valueOfHighestBit == value) {
            return value;
        }
        return valueOfHighestBit << 1;
    }

    public long getMillisUntilNextBucket() {
        long millis = System.currentTimeMillis();
        long nextTimeBucketMillis = ((millis + ((long) Math.pow(2.0d, this.numBits))) >> this.numBits) << this.numBits;
        long delta = nextTimeBucketMillis - millis;
        return delta;
    }

    public void destroy() {
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        if (this.maintenanceFuture != null) {
            this.maintenanceFuture.cancel(true);
            this.maintenanceFuture = null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/TimeBucketCounter$Maintenance.class */
    private class Maintenance implements Runnable {
        private Maintenance() {
        }

        @Override // java.lang.Runnable
        public void run() {
            String currentBucketPrefix = String.valueOf(TimeBucketCounter.this.getCurrentBucketPrefix());
            ConcurrentHashMap.KeySetView<String, AtomicInteger> keys = TimeBucketCounter.this.map.keySet();
            keys.removeIf(k -> {
                return !k.startsWith(currentBucketPrefix);
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/TimeBucketCounter$MaintenanceMonitor.class */
    private class MaintenanceMonitor implements Runnable {
        private MaintenanceMonitor() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (TimeBucketCounter.this.sleeptime > 0) {
                if (TimeBucketCounter.this.maintenanceFuture == null || TimeBucketCounter.this.maintenanceFuture.isDone()) {
                    if (TimeBucketCounter.this.maintenanceFuture != null && TimeBucketCounter.this.maintenanceFuture.isDone()) {
                        try {
                            TimeBucketCounter.this.maintenanceFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            TimeBucketCounter.log.error(TimeBucketCounter.sm.getString("timebucket.maintenance.error"), e);
                        }
                    }
                    TimeBucketCounter.this.maintenanceFuture = TimeBucketCounter.this.executorService.scheduleWithFixedDelay(new Maintenance(), TimeBucketCounter.this.sleeptime, TimeBucketCounter.this.sleeptime, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
