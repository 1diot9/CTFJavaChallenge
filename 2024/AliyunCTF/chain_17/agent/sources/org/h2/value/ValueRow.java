package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.result.SimpleResult;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueRow.class */
public final class ValueRow extends ValueCollectionBase {
    public static final ValueRow EMPTY = get(Value.EMPTY_VALUES);
    private TypeInfo type;

    private ValueRow(TypeInfo typeInfo, Value[] valueArr) {
        super(valueArr);
        int length = valueArr.length;
        if (length > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        if (typeInfo != null) {
            if (typeInfo.getValueType() != 41 || ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields().size() != length) {
                throw DbException.getInternalError();
            }
            this.type = typeInfo;
        }
    }

    public static ValueRow get(Value[] valueArr) {
        return new ValueRow(null, valueArr);
    }

    public static ValueRow get(ExtTypeInfoRow extTypeInfoRow, Value[] valueArr) {
        return new ValueRow(new TypeInfo(41, -1L, -1, extTypeInfoRow), valueArr);
    }

    public static ValueRow get(TypeInfo typeInfo, Value[] valueArr) {
        return new ValueRow(typeInfo, valueArr);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = TypeInfo.getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(this.values));
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 41;
    }

    @Override // org.h2.value.Value
    public String getString() {
        StringBuilder sb = new StringBuilder("ROW (");
        for (int i = 0; i < this.values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.values[i].getString());
        }
        return sb.append(')').toString();
    }

    public SimpleResult getResult() {
        SimpleResult simpleResult = new SimpleResult();
        int i = 0;
        int length = this.values.length;
        while (i < length) {
            int i2 = i;
            i++;
            simpleResult.addColumn("C" + i, this.values[i2].getType());
        }
        simpleResult.addRow(this.values);
        return simpleResult;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        ValueRow valueRow = (ValueRow) value;
        if (this.values == valueRow.values) {
            return 0;
        }
        int length = this.values.length;
        if (length != valueRow.values.length) {
            throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
        }
        for (int i = 0; i < length; i++) {
            int compareTo = this.values[i].compareTo(valueRow.values[i], castDataProvider, compareMode);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return 0;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append("ROW (");
        int length = this.values.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            this.values[i2].getSQL(sb, i);
        }
        return sb.append(')');
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueRow)) {
            return false;
        }
        ValueRow valueRow = (ValueRow) obj;
        if (this.values == valueRow.values) {
            return true;
        }
        int length = this.values.length;
        if (length != valueRow.values.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!this.values[i].equals(valueRow.values[i])) {
                return false;
            }
        }
        return true;
    }
}
