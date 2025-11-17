package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueArray.class */
public final class ValueArray extends ValueCollectionBase {
    public static final ValueArray EMPTY = get(TypeInfo.TYPE_NULL, Value.EMPTY_VALUES, null);
    private TypeInfo type;
    private final TypeInfo componentType;

    private ValueArray(TypeInfo typeInfo, Value[] valueArr, CastDataProvider castDataProvider) {
        super(valueArr);
        int length = valueArr.length;
        if (length > 65536) {
            String typeName = getTypeName(getValueType());
            throw DbException.getValueTooLongException(typeName, typeName, length);
        }
        for (int i = 0; i < length; i++) {
            valueArr[i] = valueArr[i].castTo(typeInfo, castDataProvider);
        }
        this.componentType = typeInfo;
    }

    public static ValueArray get(Value[] valueArr, CastDataProvider castDataProvider) {
        return new ValueArray(TypeInfo.getHigherType(valueArr), valueArr, castDataProvider);
    }

    public static ValueArray get(TypeInfo typeInfo, Value[] valueArr, CastDataProvider castDataProvider) {
        return new ValueArray(typeInfo, valueArr, castDataProvider);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = TypeInfo.getTypeInfo(getValueType(), this.values.length, 0, getComponentType());
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 40;
    }

    public TypeInfo getComponentType() {
        return this.componentType;
    }

    @Override // org.h2.value.Value
    public String getString() {
        StringBuilder append = new StringBuilder().append('[');
        for (int i = 0; i < this.values.length; i++) {
            if (i > 0) {
                append.append(", ");
            }
            append.append(this.values[i].getString());
        }
        return append.append(']').toString();
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        ValueArray valueArray = (ValueArray) value;
        if (this.values == valueArray.values) {
            return 0;
        }
        int length = this.values.length;
        int length2 = valueArray.values.length;
        int min = Math.min(length, length2);
        for (int i = 0; i < min; i++) {
            int compareTo = this.values[i].compareTo(valueArray.values[i], castDataProvider, compareMode);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return Integer.compare(length, length2);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append("ARRAY [");
        int length = this.values.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            this.values[i2].getSQL(sb, i);
        }
        return sb.append(']');
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueArray)) {
            return false;
        }
        ValueArray valueArray = (ValueArray) obj;
        if (this.values == valueArray.values) {
            return true;
        }
        int length = this.values.length;
        if (length != valueArray.values.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!this.values[i].equals(valueArray.values[i])) {
                return false;
            }
        }
        return true;
    }
}
