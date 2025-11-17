package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueCollectionBase.class */
public abstract class ValueCollectionBase extends Value {
    final Value[] values;
    private int hash;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueCollectionBase(Value[] valueArr) {
        this.values = valueArr;
    }

    public Value[] getList() {
        return this.values;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        if (this.hash != 0) {
            return this.hash;
        }
        int valueType = getValueType();
        for (Value value : this.values) {
            valueType = (valueType * 31) + value.hashCode();
        }
        this.hash = valueType;
        return valueType;
    }

    @Override // org.h2.value.Value
    public int compareWithNull(Value value, boolean z, CastDataProvider castDataProvider, CompareMode compareMode) {
        if (value == ValueNull.INSTANCE) {
            return Integer.MIN_VALUE;
        }
        int valueType = getValueType();
        if (value.getValueType() != valueType) {
            throw value.getDataConversionError(valueType);
        }
        Value[] valueArr = this.values;
        Value[] valueArr2 = ((ValueCollectionBase) value).values;
        int length = valueArr.length;
        int length2 = valueArr2.length;
        if (length != length2) {
            if (valueType == 41) {
                throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
            }
            if (z) {
                return 1;
            }
        }
        if (z) {
            boolean z2 = false;
            for (int i = 0; i < length; i++) {
                int compareWithNull = valueArr[i].compareWithNull(valueArr2[i], z, castDataProvider, compareMode);
                if (compareWithNull != 0) {
                    if (compareWithNull != Integer.MIN_VALUE) {
                        return compareWithNull;
                    }
                    z2 = true;
                }
            }
            return z2 ? Integer.MIN_VALUE : 0;
        }
        int min = Math.min(length, length2);
        for (int i2 = 0; i2 < min; i2++) {
            int compareWithNull2 = valueArr[i2].compareWithNull(valueArr2[i2], z, castDataProvider, compareMode);
            if (compareWithNull2 != 0) {
                return compareWithNull2;
            }
        }
        return Integer.compare(length, length2);
    }

    @Override // org.h2.value.Value
    public boolean containsNull() {
        for (Value value : this.values) {
            if (value.containsNull()) {
                return true;
            }
        }
        return false;
    }

    @Override // org.h2.value.Value
    Value getValueWithFirstNullImpl(Value value) {
        Value[] valueArr = this.values;
        Value[] valueArr2 = ((ValueCollectionBase) value).values;
        int min = Math.min(valueArr.length, valueArr2.length);
        for (int i = 0; i < min; i++) {
            Value value2 = valueArr[i];
            Value value3 = valueArr2[i];
            Value valueWithFirstNull = value2.getValueWithFirstNull(value3);
            if (valueWithFirstNull == value2) {
                return this;
            }
            if (valueWithFirstNull == value3) {
                return value;
            }
        }
        return null;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        int length = 72 + (this.values.length * 8);
        for (Value value : this.values) {
            length += value.getMemory();
        }
        return length;
    }
}
