package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBoolean.class */
public final class ValueBoolean extends Value {
    public static final int PRECISION = 1;
    public static final int DISPLAY_SIZE = 5;
    public static final ValueBoolean TRUE = new ValueBoolean(true);
    public static final ValueBoolean FALSE = new ValueBoolean(false);
    private final boolean value;

    private ValueBoolean(boolean z) {
        this.value = z;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_BOOLEAN;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 8;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 0;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(getString());
    }

    @Override // org.h2.value.Value
    public String getString() {
        return this.value ? Constants.CLUSTERING_ENABLED : "FALSE";
    }

    @Override // org.h2.value.Value
    public boolean getBoolean() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public byte getByte() {
        return this.value ? (byte) 1 : (byte) 0;
    }

    @Override // org.h2.value.Value
    public short getShort() {
        return this.value ? (short) 1 : (short) 0;
    }

    @Override // org.h2.value.Value
    public int getInt() {
        return this.value ? 1 : 0;
    }

    @Override // org.h2.value.Value
    public long getLong() {
        return this.value ? 1L : 0L;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        return this.value ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        return this.value ? 1.0f : 0.0f;
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        return this.value ? 1.0d : 0.0d;
    }

    @Override // org.h2.value.Value
    public Value negate() {
        return this.value ? FALSE : TRUE;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Boolean.compare(this.value, ((ValueBoolean) value).value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return this.value ? 1 : 0;
    }

    public static ValueBoolean get(boolean z) {
        return z ? TRUE : FALSE;
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return this == obj;
    }
}
