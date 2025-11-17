package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueVarcharIgnoreCase.class */
public final class ValueVarcharIgnoreCase extends ValueStringBase {
    private static final ValueVarcharIgnoreCase EMPTY = new ValueVarcharIgnoreCase("");
    private int hash;

    private ValueVarcharIgnoreCase(String str) {
        super(str);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 4;
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return compareMode.compareString(this.value, ((ValueStringBase) value).value, true);
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueVarcharIgnoreCase) && this.value.equalsIgnoreCase(((ValueVarcharIgnoreCase) obj).value);
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public int hashCode() {
        if (this.hash == 0) {
            this.hash = this.value.toUpperCase().hashCode();
        }
        return this.hash;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return StringUtils.quoteStringSQL(sb.append("CAST("), this.value).append(" AS VARCHAR_IGNORECASE(").append(this.value.length()).append("))");
        }
        return StringUtils.quoteStringSQL(sb, this.value);
    }

    public static ValueVarcharIgnoreCase get(String str) {
        int length = str.length();
        if (length == 0) {
            return EMPTY;
        }
        ValueVarcharIgnoreCase valueVarcharIgnoreCase = new ValueVarcharIgnoreCase(StringUtils.cache(str));
        if (length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueVarcharIgnoreCase;
        }
        ValueVarcharIgnoreCase valueVarcharIgnoreCase2 = (ValueVarcharIgnoreCase) Value.cache(valueVarcharIgnoreCase);
        if (valueVarcharIgnoreCase2.value.equals(str)) {
            return valueVarcharIgnoreCase2;
        }
        return valueVarcharIgnoreCase;
    }
}
