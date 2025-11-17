package org.h2.mvstore;

import cn.hutool.core.text.CharSequenceUtil;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/RootReference.class */
public final class RootReference<K, V> {
    public final Page<K, V> root;
    public final long version;
    private final byte holdCount;
    private final long ownerId;
    volatile RootReference<K, V> previous;
    final long updateCounter;
    final long updateAttemptCounter;
    private final byte appendCounter;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !RootReference.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference(Page<K, V> page, long j) {
        this.root = page;
        this.version = j;
        this.previous = null;
        this.updateCounter = 1L;
        this.updateAttemptCounter = 1L;
        this.holdCount = (byte) 0;
        this.ownerId = 0L;
        this.appendCounter = (byte) 0;
    }

    private RootReference(RootReference<K, V> rootReference, Page<K, V> page, long j) {
        this.root = page;
        this.version = rootReference.version;
        this.previous = rootReference.previous;
        this.updateCounter = rootReference.updateCounter + 1;
        this.updateAttemptCounter = rootReference.updateAttemptCounter + j;
        this.holdCount = (byte) 0;
        this.ownerId = 0L;
        this.appendCounter = rootReference.appendCounter;
    }

    private RootReference(RootReference<K, V> rootReference, int i) {
        this.root = rootReference.root;
        this.version = rootReference.version;
        this.previous = rootReference.previous;
        this.updateCounter = rootReference.updateCounter + 1;
        this.updateAttemptCounter = rootReference.updateAttemptCounter + i;
        if (!$assertionsDisabled && rootReference.holdCount != 0 && rootReference.ownerId != Thread.currentThread().getId()) {
            throw new AssertionError(Thread.currentThread().getId() + CharSequenceUtil.SPACE + rootReference);
        }
        this.holdCount = (byte) (rootReference.holdCount + 1);
        this.ownerId = Thread.currentThread().getId();
        this.appendCounter = rootReference.appendCounter;
    }

    private RootReference(RootReference<K, V> rootReference, Page<K, V> page, boolean z, int i) {
        this.root = page;
        this.version = rootReference.version;
        this.previous = rootReference.previous;
        this.updateCounter = rootReference.updateCounter;
        this.updateAttemptCounter = rootReference.updateAttemptCounter;
        if (!$assertionsDisabled && (rootReference.holdCount <= 0 || rootReference.ownerId != Thread.currentThread().getId())) {
            throw new AssertionError(Thread.currentThread().getId() + CharSequenceUtil.SPACE + rootReference);
        }
        this.holdCount = (byte) (rootReference.holdCount - (z ? (byte) 0 : (byte) 1));
        this.ownerId = this.holdCount == 0 ? 0L : Thread.currentThread().getId();
        this.appendCounter = (byte) i;
    }

    private RootReference(RootReference<K, V> rootReference, long j, int i) {
        RootReference<K, V> rootReference2;
        RootReference<K, V> rootReference3 = rootReference;
        while (true) {
            rootReference2 = rootReference3;
            RootReference<K, V> rootReference4 = rootReference2.previous;
            if (rootReference4 == null || rootReference4.root != rootReference.root) {
                break;
            } else {
                rootReference3 = rootReference4;
            }
        }
        this.root = rootReference.root;
        this.version = j;
        this.previous = rootReference2;
        this.updateCounter = rootReference.updateCounter + 1;
        this.updateAttemptCounter = rootReference.updateAttemptCounter + i;
        this.holdCount = rootReference.holdCount == 0 ? (byte) 0 : (byte) (rootReference.holdCount - 1);
        this.ownerId = this.holdCount == 0 ? 0L : rootReference.ownerId;
        if (!$assertionsDisabled && rootReference.appendCounter != 0) {
            throw new AssertionError();
        }
        this.appendCounter = (byte) 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<K, V> updateRootPage(Page<K, V> page, long j) {
        if (isFree()) {
            return tryUpdate(new RootReference<>(this, page, j));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<K, V> tryLock(int i) {
        if (canUpdate()) {
            return tryUpdate(new RootReference<>(this, i));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<K, V> tryUnlockAndUpdateVersion(long j, int i) {
        if (canUpdate()) {
            return tryUpdate(new RootReference<>(this, j, i));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<K, V> updatePageAndLockedStatus(Page<K, V> page, boolean z, int i) {
        if (canUpdate()) {
            return tryUpdate(new RootReference<>(this, page, z, i));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUnusedOldVersions(long j) {
        RootReference<K, V> rootReference;
        RootReference<K, V> rootReference2 = this;
        while (true) {
            RootReference<K, V> rootReference3 = rootReference2;
            if (rootReference3 != null) {
                if (rootReference3.version < j) {
                    if (!$assertionsDisabled && (rootReference = rootReference3.previous) != null && rootReference.getAppendCounter() != 0) {
                        throw new AssertionError(j + CharSequenceUtil.SPACE + rootReference3.previous);
                    }
                    rootReference3.previous = null;
                }
                rootReference2 = rootReference3.previous;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLocked() {
        return this.holdCount != 0;
    }

    private boolean isFree() {
        return this.holdCount == 0;
    }

    private boolean canUpdate() {
        return isFree() || this.ownerId == Thread.currentThread().getId();
    }

    public boolean isLockedByCurrentThread() {
        return this.holdCount != 0 && this.ownerId == Thread.currentThread().getId();
    }

    private RootReference<K, V> tryUpdate(RootReference<K, V> rootReference) {
        if (!$assertionsDisabled && !canUpdate()) {
            throw new AssertionError();
        }
        if (this.root.map.compareAndSetRoot(this, rootReference)) {
            return rootReference;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getVersion() {
        RootReference<K, V> rootReference = this.previous;
        return (rootReference != null && rootReference.root == this.root && rootReference.appendCounter == this.appendCounter) ? rootReference.getVersion() : this.version;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasChangesSince(long j, boolean z) {
        return (z && (!this.root.isSaved() ? getTotalCount() <= 0 : getAppendCounter() <= 0)) || getVersion() > j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAppendCounter() {
        return this.appendCounter & 255;
    }

    public boolean needFlush() {
        return this.appendCounter != 0;
    }

    public long getTotalCount() {
        return this.root.getTotalCount() + getAppendCounter();
    }

    public String toString() {
        return "RootReference(" + System.identityHashCode(this.root) + ", v=" + this.version + ", owner=" + this.ownerId + (this.ownerId == Thread.currentThread().getId() ? "(current)" : "") + ", holdCnt=" + ((int) this.holdCount) + ", keys=" + this.root.getTotalCount() + ", append=" + getAppendCounter() + ")";
    }
}
