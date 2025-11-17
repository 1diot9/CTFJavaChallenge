package org.springframework.boot.loader.zip;

import java.util.BitSet;

/* loaded from: server.jar:org/springframework/boot/loader/zip/NameOffsetLookups.class */
class NameOffsetLookups {
    public static final NameOffsetLookups NONE = new NameOffsetLookups(0, 0);
    private final int offset;
    private final BitSet enabled;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NameOffsetLookups(int offset, int size) {
        this.offset = offset;
        this.enabled = size != 0 ? new BitSet(size) : null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void swap(int i, int j) {
        if (this.enabled != null) {
            boolean temp = this.enabled.get(i);
            this.enabled.set(i, this.enabled.get(j));
            this.enabled.set(j, temp);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int get(int index) {
        if (isEnabled(index)) {
            return this.offset;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int enable(int index, boolean enable) {
        if (this.enabled != null) {
            this.enabled.set(index, enable);
        }
        if (enable) {
            return this.offset;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnabled(int index) {
        return this.enabled != null && this.enabled.get(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAnyEnabled() {
        return this.enabled != null && this.enabled.cardinality() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NameOffsetLookups emptyCopy() {
        return new NameOffsetLookups(this.offset, this.enabled.size());
    }
}
