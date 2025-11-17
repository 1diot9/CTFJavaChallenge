package org.h2.util;

import cn.hutool.core.text.StrPool;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/IntArray.class */
public class IntArray {
    private int[] data;
    private int size;
    private int hash;

    public IntArray() {
        this(10);
    }

    public IntArray(int i) {
        this.data = new int[i];
    }

    public IntArray(int[] iArr) {
        this.data = iArr;
        this.size = iArr.length;
    }

    public void add(int i) {
        if (this.size >= this.data.length) {
            ensureCapacity(this.size + this.size);
        }
        int[] iArr = this.data;
        int i2 = this.size;
        this.size = i2 + 1;
        iArr[i2] = i;
    }

    public int get(int i) {
        if (i >= this.size) {
            throw new ArrayIndexOutOfBoundsException("i=" + i + " size=" + this.size);
        }
        return this.data[i];
    }

    public void remove(int i) {
        if (i >= this.size) {
            throw new ArrayIndexOutOfBoundsException("i=" + i + " size=" + this.size);
        }
        System.arraycopy(this.data, i + 1, this.data, i, (this.size - i) - 1);
        this.size--;
    }

    public void ensureCapacity(int i) {
        int max = Math.max(4, i);
        if (max >= this.data.length) {
            this.data = Arrays.copyOf(this.data, max);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IntArray)) {
            return false;
        }
        IntArray intArray = (IntArray) obj;
        if (hashCode() != intArray.hashCode() || this.size != intArray.size) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] != intArray.data[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (this.hash != 0) {
            return this.hash;
        }
        int i = this.size + 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + this.data[i2];
        }
        this.hash = i;
        return i;
    }

    public int size() {
        return this.size;
    }

    public void toArray(int[] iArr) {
        System.arraycopy(this.data, 0, iArr, 0, this.size);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(StrPool.DELIM_START);
        for (int i = 0; i < this.size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.data[i]);
        }
        return sb.append('}').toString();
    }

    public void removeRange(int i, int i2) {
        if (i > i2 || i2 > this.size) {
            throw new ArrayIndexOutOfBoundsException("from=" + i + " to=" + i2 + " size=" + this.size);
        }
        System.arraycopy(this.data, i2, this.data, i, this.size - i2);
        this.size -= i2 - i;
    }
}
