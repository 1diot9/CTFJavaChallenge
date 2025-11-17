package org.h2.result;

import org.h2.engine.CastDataProvider;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/SearchRow.class */
public abstract class SearchRow extends Value {
    public static final int ROWID_INDEX = -1;
    public static long MATCH_ALL_ROW_KEY = -9223372036854775807L;
    public static final int MEMORY_CALCULATE = -1;
    protected long key;

    public abstract int getColumnCount();

    public abstract Value getValue(int i);

    public abstract void setValue(int i, Value value);

    @Override // org.h2.value.Value
    public abstract int getMemory();

    public abstract void copyFrom(SearchRow searchRow);

    public boolean isNull(int i) {
        return getValue(i) == ValueNull.INSTANCE;
    }

    public void setKey(long j) {
        this.key = j;
    }

    public long getKey() {
        return this.key;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_ROW_EMPTY;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 41;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append("ROW (");
        int columnCount = getColumnCount();
        for (int i2 = 0; i2 < columnCount; i2++) {
            if (i2 != 0) {
                sb.append(", ");
            }
            getValue(i2).getSQL(sb, i);
        }
        return sb.append(')');
    }

    @Override // org.h2.value.Value
    public String getString() {
        return getTraceSQL();
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        throw new UnsupportedOperationException();
    }
}
