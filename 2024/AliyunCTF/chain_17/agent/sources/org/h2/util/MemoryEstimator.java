package org.h2.util;

import java.util.concurrent.atomic.AtomicLong;
import org.h2.mvstore.type.DataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/MemoryEstimator.class */
public final class MemoryEstimator {
    private static final int SKIP_SUM_SHIFT = 8;
    private static final int COUNTER_MASK = 255;
    private static final int SKIP_SUM_MASK = 65535;
    private static final int INIT_BIT_SHIFT = 24;
    private static final int INIT_BIT = 16777216;
    private static final int WINDOW_SHIFT = 8;
    private static final int MAGNITUDE_LIMIT = 7;
    private static final int WINDOW_SIZE = 256;
    private static final int WINDOW_HALF_SIZE = 128;
    private static final int SUM_SHIFT = 32;

    private MemoryEstimator() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x0030, code lost:            if (r17 == 0) goto L6;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static <T> int estimateMemory(java.util.concurrent.atomic.AtomicLong r12, org.h2.mvstore.type.DataType<T> r13, T r14) {
        /*
            Method dump skipped, instructions count: 236
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.MemoryEstimator.estimateMemory(java.util.concurrent.atomic.AtomicLong, org.h2.mvstore.type.DataType, java.lang.Object):int");
    }

    public static <T> int estimateMemory(AtomicLong atomicLong, DataType<T> dataType, T[] tArr, int i) {
        long j = atomicLong.get();
        int counter = getCounter(j);
        int skipSum = getSkipSum(j);
        long j2 = j & 16777216;
        long j3 = j >>> 32;
        int i2 = 0;
        int i3 = 0;
        if (j2 != 0 && counter >= i) {
            counter -= i;
        } else {
            int i4 = i;
            while (true) {
                int i5 = i4;
                i4--;
                if (i5 <= 0) {
                    break;
                }
                int i6 = i2;
                i2++;
                T t = tArr[i6];
                int memory = t == null ? 0 : dataType.getMemory(t);
                i3 += memory;
                long j4 = (memory << 8) - j3;
                if (j2 == 0) {
                    counter++;
                    if (counter == 256) {
                        j2 = 16777216;
                    }
                    j3 = (((j3 * counter) + j4) + (counter >> 1)) / counter;
                } else {
                    i4 -= counter;
                    int calculateMagnitude = calculateMagnitude(j3, j4 >= 0 ? j4 : -j4);
                    j3 += ((j4 >> (7 - calculateMagnitude)) + 1) >> 1;
                    counter += ((1 << calculateMagnitude) - 1) & 255;
                    skipSum = (int) (skipSum + ((((counter << 8) - skipSum) + 128) >> 8));
                }
            }
        }
        return (getAverage(updateStatsData(atomicLong, j, counter, skipSum, j2, j3, i2, i3)) + 8) * i;
    }

    public static int samplingPct(AtomicLong atomicLong) {
        long j = atomicLong.get();
        int counter = (j & 16777216) == 0 ? getCounter(j) : 256;
        int skipSum = getSkipSum(j) + counter;
        return ((counter * 100) + (skipSum >> 1)) / skipSum;
    }

    private static int calculateMagnitude(long j, long j2) {
        int i = 0;
        while (j2 < j && i < 7) {
            i++;
            j2 <<= 1;
        }
        return i;
    }

    private static long updateStatsData(AtomicLong atomicLong, long j, int i, int i2, long j2, long j3, int i3, int i4) {
        return updateStatsData(atomicLong, j, constructStatsData(j3, j2, i2, i), i3, i4);
    }

    private static long constructStatsData(long j, long j2, int i, int i2) {
        return (j << 32) | j2 | (i << 8) | i2;
    }

    private static long updateStatsData(AtomicLong atomicLong, long j, long j2, int i, int i2) {
        while (!atomicLong.compareAndSet(j, j2)) {
            j = atomicLong.get();
            long j3 = j >>> 32;
            if (i > 0) {
                j3 += i2 - (((j3 * i) + 128) >> 8);
            }
            j2 = (j3 << 32) | (j & 16842751);
        }
        return j2;
    }

    private static int getCounter(long j) {
        return (int) (j & 255);
    }

    private static int getSkipSum(long j) {
        return (int) ((j >> 8) & 65535);
    }

    private static int getAverage(long j) {
        return (int) (j >>> 40);
    }
}
