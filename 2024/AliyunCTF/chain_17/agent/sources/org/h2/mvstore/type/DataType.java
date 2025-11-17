package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import java.util.Comparator;
import org.h2.mvstore.WriteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/DataType.class */
public interface DataType<T> extends Comparator<T> {
    @Override // java.util.Comparator
    int compare(T t, T t2);

    int binarySearch(T t, Object obj, int i, int i2);

    int getMemory(T t);

    boolean isMemoryEstimationAllowed();

    void write(WriteBuffer writeBuffer, T t);

    void write(WriteBuffer writeBuffer, Object obj, int i);

    T read(ByteBuffer byteBuffer);

    void read(ByteBuffer byteBuffer, Object obj, int i);

    T[] createStorage(int i);
}
