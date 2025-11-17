package org.h2.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/ByteStack.class */
public final class ByteStack {
    private static final int MAX_ARRAY_SIZE = 2147483639;
    private int size;
    private byte[] array = Utils.EMPTY_BYTES;

    public void push(byte b) {
        int i = this.size;
        int length = this.array.length;
        if (i >= length) {
            grow(length);
        }
        this.array[i] = b;
        this.size = i + 1;
    }

    public byte pop() {
        int i = this.size - 1;
        if (i < 0) {
            throw new NoSuchElementException();
        }
        this.size = i;
        return this.array[i];
    }

    public int poll(int i) {
        int i2 = this.size - 1;
        if (i2 < 0) {
            return i;
        }
        this.size = i2;
        return this.array[i2];
    }

    public int peek(int i) {
        int i2 = this.size - 1;
        if (i2 < 0) {
            return i;
        }
        return this.array[i2];
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    private void grow(int i) {
        int i2;
        if (i == 0) {
            i2 = 16;
        } else {
            if (i >= 2147483639) {
                throw new OutOfMemoryError();
            }
            int i3 = i << 1;
            i2 = i3;
            if (i3 < 0) {
                i2 = 2147483639;
            }
        }
        this.array = Arrays.copyOf(this.array, i2);
    }
}
