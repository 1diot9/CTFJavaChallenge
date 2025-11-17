package com.fasterxml.jackson.core.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/ThreadLocalBufferManager.class */
class ThreadLocalBufferManager {
    private final Object RELEASE_LOCK = new Object();
    private final Map<SoftReference<BufferRecycler>, Boolean> _trackedRecyclers = new ConcurrentHashMap();
    private final ReferenceQueue<BufferRecycler> _refQueue = new ReferenceQueue<>();

    ThreadLocalBufferManager() {
    }

    public static ThreadLocalBufferManager instance() {
        return ThreadLocalBufferManagerHolder.manager;
    }

    public int releaseBuffers() {
        int i;
        synchronized (this.RELEASE_LOCK) {
            int count = 0;
            removeSoftRefsClearedByGc();
            for (SoftReference<BufferRecycler> ref : this._trackedRecyclers.keySet()) {
                ref.clear();
                count++;
            }
            this._trackedRecyclers.clear();
            i = count;
        }
        return i;
    }

    public SoftReference<BufferRecycler> wrapAndTrack(BufferRecycler br) {
        SoftReference<BufferRecycler> newRef = new SoftReference<>(br, this._refQueue);
        this._trackedRecyclers.put(newRef, true);
        removeSoftRefsClearedByGc();
        return newRef;
    }

    private void removeSoftRefsClearedByGc() {
        while (true) {
            SoftReference<?> clearedSoftRef = (SoftReference) this._refQueue.poll();
            if (clearedSoftRef != null) {
                this._trackedRecyclers.remove(clearedSoftRef);
            } else {
                return;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/ThreadLocalBufferManager$ThreadLocalBufferManagerHolder.class */
    private static final class ThreadLocalBufferManagerHolder {
        static final ThreadLocalBufferManager manager = new ThreadLocalBufferManager();

        private ThreadLocalBufferManagerHolder() {
        }
    }
}
