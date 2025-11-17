package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/RingIndexUtil.class */
public class RingIndexUtil {
    public static int ringNextIntByObj(Object object, AtomicInteger atomicInteger) {
        Assert.notNull(object);
        int modulo = CollUtil.size(object);
        return ringNextInt(modulo, atomicInteger);
    }

    public static int ringNextInt(int modulo, AtomicInteger atomicInteger) {
        int current;
        int next;
        Assert.notNull(atomicInteger);
        Assert.isTrue(modulo > 0);
        if (modulo <= 1) {
            return 0;
        }
        do {
            current = atomicInteger.get();
            next = (current + 1) % modulo;
        } while (!atomicInteger.compareAndSet(current, next));
        return next;
    }

    public static long ringNextLong(long modulo, AtomicLong atomicLong) {
        long current;
        long next;
        Assert.notNull(atomicLong);
        Assert.isTrue(modulo > 0);
        if (modulo <= 1) {
            return 0L;
        }
        do {
            current = atomicLong.get();
            next = (current + 1) % modulo;
        } while (!atomicLong.compareAndSet(current, next));
        return next;
    }
}
