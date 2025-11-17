package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/BasicDataType.class */
public abstract class BasicDataType<T> implements DataType<T> {
    @Override // org.h2.mvstore.type.DataType
    public abstract int getMemory(T t);

    @Override // org.h2.mvstore.type.DataType
    public abstract void write(WriteBuffer writeBuffer, T t);

    @Override // org.h2.mvstore.type.DataType
    public abstract T read(ByteBuffer byteBuffer);

    @Override // org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(T t, T t2) {
        throw DataUtils.newUnsupportedOperationException("Can not compare");
    }

    @Override // org.h2.mvstore.type.DataType
    public boolean isMemoryEstimationAllowed() {
        return true;
    }

    @Override // org.h2.mvstore.type.DataType
    public int binarySearch(T t, Object obj, int i, int i2) {
        T[] cast = cast(obj);
        int i3 = 0;
        int i4 = i - 1;
        int i5 = i2 - 1;
        if (i5 < 0 || i5 > i4) {
            i5 = i4 >>> 1;
        }
        while (i3 <= i4) {
            int compare = compare(t, cast[i5]);
            if (compare > 0) {
                i3 = i5 + 1;
            } else if (compare < 0) {
                i4 = i5 - 1;
            } else {
                return i5;
            }
            i5 = (i3 + i4) >>> 1;
        }
        return i3 ^ (-1);
    }

    @Override // org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Object obj, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            write(writeBuffer, cast(obj)[i2]);
        }
    }

    @Override // org.h2.mvstore.type.DataType
    public void read(ByteBuffer byteBuffer, Object obj, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            cast(obj)[i2] = read(byteBuffer);
        }
    }

    public int hashCode() {
        return getClass().getName().hashCode();
    }

    @Override // java.util.Comparator
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final T[] cast(Object obj) {
        return (T[]) ((Object[]) obj);
    }
}
