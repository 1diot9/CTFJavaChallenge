package cn.hutool.core.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/lock/NoReadWriteLock.class */
public class NoReadWriteLock implements ReadWriteLock {
    @Override // java.util.concurrent.locks.ReadWriteLock
    public Lock readLock() {
        return NoLock.INSTANCE;
    }

    @Override // java.util.concurrent.locks.ReadWriteLock
    public Lock writeLock() {
        return NoLock.INSTANCE;
    }
}
