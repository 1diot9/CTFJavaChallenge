package org.h2.util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DebuggingThreadLocal.class */
public class DebuggingThreadLocal<T> {
    private final ConcurrentHashMap<Long, T> map = new ConcurrentHashMap<>();

    public void set(T t) {
        this.map.put(Long.valueOf(Thread.currentThread().getId()), t);
    }

    public void remove() {
        this.map.remove(Long.valueOf(Thread.currentThread().getId()));
    }

    public T get() {
        return this.map.get(Long.valueOf(Thread.currentThread().getId()));
    }

    public HashMap<Long, T> getSnapshotOfAllThreads() {
        return new HashMap<>(this.map);
    }
}
