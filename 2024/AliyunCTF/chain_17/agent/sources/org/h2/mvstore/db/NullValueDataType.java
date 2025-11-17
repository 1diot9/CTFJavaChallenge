package org.h2.mvstore.db;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.DataType;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/NullValueDataType.class */
public final class NullValueDataType implements DataType<Value> {
    public static final NullValueDataType INSTANCE = new NullValueDataType();

    private NullValueDataType() {
    }

    @Override // org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(Value value, Value value2) {
        return 0;
    }

    @Override // org.h2.mvstore.type.DataType
    public int binarySearch(Value value, Object obj, int i, int i2) {
        return 0;
    }

    @Override // org.h2.mvstore.type.DataType
    public int getMemory(Value value) {
        return 0;
    }

    @Override // org.h2.mvstore.type.DataType
    public boolean isMemoryEstimationAllowed() {
        return true;
    }

    @Override // org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Value value) {
    }

    @Override // org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Object obj, int i) {
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.type.DataType
    public Value read(ByteBuffer byteBuffer) {
        return ValueNull.INSTANCE;
    }

    @Override // org.h2.mvstore.type.DataType
    public void read(ByteBuffer byteBuffer, Object obj, int i) {
        Arrays.fill((Value[]) obj, 0, i, ValueNull.INSTANCE);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.type.DataType
    public Value[] createStorage(int i) {
        return new Value[i];
    }
}
