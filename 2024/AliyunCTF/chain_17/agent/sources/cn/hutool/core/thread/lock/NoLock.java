package cn.hutool.core.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/lock/NoLock.class */
public class NoLock implements Lock {
    public static NoLock INSTANCE = new NoLock();

    @Override // java.util.concurrent.locks.Lock
    public void lock() {
    }

    @Override // java.util.concurrent.locks.Lock
    public void lockInterruptibly() {
    }

    @Override // java.util.concurrent.locks.Lock
    public boolean tryLock() {
        return true;
    }

    @Override // java.util.concurrent.locks.Lock
    public boolean tryLock(long time, TimeUnit unit) {
        return true;
    }

    @Override // java.util.concurrent.locks.Lock
    public void unlock() {
    }

    @Override // java.util.concurrent.locks.Lock
    public Condition newCondition() {
        throw new UnsupportedOperationException("NoLock`s newCondition method is unsupported");
    }
}
