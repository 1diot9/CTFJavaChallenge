package org.h2.util;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Permutations.class */
public class Permutations<T> {
    private final T[] in;
    private final T[] out;
    private final int n;
    private final int m;
    private final int[] index;
    private boolean hasNext = true;

    private Permutations(T[] tArr, T[] tArr2, int i) {
        this.n = tArr.length;
        this.m = i;
        if (this.n < i || i < 0) {
            throw DbException.getInternalError("n < m or m < 0");
        }
        this.in = tArr;
        this.out = tArr2;
        this.index = new int[this.n];
        for (int i2 = 0; i2 < this.n; i2++) {
            this.index[i2] = i2;
        }
        reverseAfter(i - 1);
    }

    public static <T> Permutations<T> create(T[] tArr, T[] tArr2) {
        return new Permutations<>(tArr, tArr2, tArr.length);
    }

    public static <T> Permutations<T> create(T[] tArr, T[] tArr2, int i) {
        return new Permutations<>(tArr, tArr2, i);
    }

    private void moveIndex() {
        int rightmostDip = rightmostDip();
        if (rightmostDip < 0) {
            this.hasNext = false;
            return;
        }
        int i = rightmostDip + 1;
        for (int i2 = rightmostDip + 2; i2 < this.n; i2++) {
            if (this.index[i2] < this.index[i] && this.index[i2] > this.index[rightmostDip]) {
                i = i2;
            }
        }
        int i3 = this.index[rightmostDip];
        this.index[rightmostDip] = this.index[i];
        this.index[i] = i3;
        if (this.m - 1 > rightmostDip) {
            reverseAfter(rightmostDip);
            reverseAfter(this.m - 1);
        }
    }

    private int rightmostDip() {
        for (int i = this.n - 2; i >= 0; i--) {
            if (this.index[i] < this.index[i + 1]) {
                return i;
            }
        }
        return -1;
    }

    private void reverseAfter(int i) {
        int i2 = i + 1;
        for (int i3 = this.n - 1; i2 < i3; i3--) {
            int i4 = this.index[i2];
            this.index[i2] = this.index[i3];
            this.index[i3] = i4;
            i2++;
        }
    }

    public boolean next() {
        if (!this.hasNext) {
            return false;
        }
        for (int i = 0; i < this.m; i++) {
            this.out[i] = this.in[this.index[i]];
        }
        moveIndex();
        return true;
    }
}
