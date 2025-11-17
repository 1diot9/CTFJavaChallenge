package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueEnumBase.class */
public class ValueEnumBase extends Value {
    final String label;
    private final int ordinal;

    /* JADX INFO: Access modifiers changed from: protected */
    public ValueEnumBase(String str, int i) {
        this.label = str;
        this.ordinal = i;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return convertToInt(null).add(value.convertToInt(null));
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Integer.compare(getInt(), value.getInt());
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        return convertToInt(null).divide(value.convertToInt(null), typeInfo);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueEnumBase) && getInt() == ((ValueEnumBase) obj).getInt();
    }

    public static ValueEnumBase get(String str, int i) {
        return new ValueEnumBase(str, i);
    }

    @Override // org.h2.value.Value
    public int getInt() {
        return this.ordinal;
    }

    @Override // org.h2.value.Value
    public long getLong() {
        return this.ordinal;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(this.ordinal);
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        return this.ordinal;
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        return this.ordinal;
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        return Integer.signum(this.ordinal);
    }

    public StringBuilder getSQL(StringBuilder sb, int i) {
        return StringUtils.quoteStringSQL(sb, this.label);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return this.label;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_ENUM_UNDEFINED;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 36;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 120;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return 31 + getString().hashCode() + getInt();
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        return convertToInt(null).modulus(value.convertToInt(null));
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return convertToInt(null).multiply(value.convertToInt(null));
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return convertToInt(null).subtract(value.convertToInt(null));
    }
}
