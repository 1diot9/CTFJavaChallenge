package cn.hutool.core.thread.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/lock/LockUtil.class */
public class LockUtil {
    private static final NoLock NO_LOCK = new NoLock();

    public static StampedLock createStampLock() {
        return new StampedLock();
    }

    public static ReentrantReadWriteLock createReadWriteLock(boolean fair) {
        return new ReentrantReadWriteLock(fair);
    }

    public static NoLock getNoLock() {
        return NO_LOCK;
    }
}
