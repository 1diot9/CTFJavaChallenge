package org.h2.mvstore.tx;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/VersionedValueType.class */
public class VersionedValueType<T, D> extends BasicDataType<VersionedValue<T>> implements StatefulDataType<D> {
    private final DataType<T> valueType;
    private final Factory<D> factory = new Factory<>();

    public VersionedValueType(DataType<T> dataType) {
        this.valueType = dataType;
    }

    @Override // org.h2.mvstore.type.DataType
    public VersionedValue<T>[] createStorage(int i) {
        return new VersionedValue[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(VersionedValue<T> versionedValue) {
        if (versionedValue == null) {
            return 0;
        }
        int valMemory = 48 + getValMemory(versionedValue.getCurrentValue());
        if (versionedValue.getOperationId() != 0) {
            valMemory += getValMemory(versionedValue.getCommittedValue());
        }
        return valMemory;
    }

    private int getValMemory(T t) {
        if (t == null) {
            return 0;
        }
        return this.valueType.getMemory(t);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void read(ByteBuffer byteBuffer, Object obj, int i) {
        if (byteBuffer.get() == 0) {
            for (int i2 = 0; i2 < i; i2++) {
                ((VersionedValue[]) cast(obj))[i2] = VersionedValueCommitted.getInstance(this.valueType.read(byteBuffer));
            }
            return;
        }
        for (int i3 = 0; i3 < i; i3++) {
            ((VersionedValue[]) cast(obj))[i3] = read(byteBuffer);
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public VersionedValue<T> read(ByteBuffer byteBuffer) {
        long readVarLong = DataUtils.readVarLong(byteBuffer);
        if (readVarLong == 0) {
            return VersionedValueCommitted.getInstance(this.valueType.read(byteBuffer));
        }
        byte b = byteBuffer.get();
        return VersionedValueUncommitted.getInstance(readVarLong, (b & 1) != 0 ? this.valueType.read(byteBuffer) : null, (b & 2) != 0 ? this.valueType.read(byteBuffer) : null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Object obj, int i) {
        boolean z = true;
        for (int i2 = 0; i2 < i; i2++) {
            VersionedValue versionedValue = ((VersionedValue[]) cast(obj))[i2];
            if (versionedValue.getOperationId() != 0 || versionedValue.getCurrentValue() == null) {
                z = false;
            }
        }
        if (z) {
            writeBuffer.put((byte) 0);
            for (int i3 = 0; i3 < i; i3++) {
                this.valueType.write(writeBuffer, ((VersionedValue[]) cast(obj))[i3].getCurrentValue());
            }
            return;
        }
        writeBuffer.put((byte) 1);
        for (int i4 = 0; i4 < i; i4++) {
            write(writeBuffer, (VersionedValue) ((VersionedValue[]) cast(obj))[i4]);
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, VersionedValue<T> versionedValue) {
        long operationId = versionedValue.getOperationId();
        writeBuffer.putVarLong(operationId);
        if (operationId == 0) {
            this.valueType.write(writeBuffer, versionedValue.getCurrentValue());
            return;
        }
        T committedValue = versionedValue.getCommittedValue();
        writeBuffer.put((byte) ((versionedValue.getCurrentValue() == null ? 0 : 1) | (committedValue == null ? 0 : 2)));
        if (versionedValue.getCurrentValue() != null) {
            this.valueType.write(writeBuffer, versionedValue.getCurrentValue());
        }
        if (committedValue != null) {
            this.valueType.write(writeBuffer, committedValue);
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, java.util.Comparator
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VersionedValueType)) {
            return false;
        }
        return this.valueType.equals(((VersionedValueType) obj).valueType);
    }

    @Override // org.h2.mvstore.type.BasicDataType
    public int hashCode() {
        return super.hashCode() ^ this.valueType.hashCode();
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    public void save(WriteBuffer writeBuffer, MetaType<D> metaType) {
        metaType.write(writeBuffer, (DataType<?>) this.valueType);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(VersionedValue<T> versionedValue, VersionedValue<T> versionedValue2) {
        return this.valueType.compare(versionedValue.getCurrentValue(), versionedValue2.getCurrentValue());
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    public Factory<D> getFactory() {
        return this.factory;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/VersionedValueType$Factory.class */
    public static final class Factory<D> implements StatefulDataType.Factory<D> {
        @Override // org.h2.mvstore.type.StatefulDataType.Factory
        public DataType<?> create(ByteBuffer byteBuffer, MetaType<D> metaType, D d) {
            return new VersionedValueType(metaType.read(byteBuffer));
        }
    }
}
