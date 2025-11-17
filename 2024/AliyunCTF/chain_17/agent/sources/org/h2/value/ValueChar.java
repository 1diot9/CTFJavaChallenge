package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueChar.class */
public final class ValueChar extends ValueStringBase {
    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    private ValueChar(String str) {
        super(str);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 1;
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return compareMode.compareString(convertToChar().getString(), value.convertToChar().getString(), false);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            int length = this.value.length();
            return StringUtils.quoteStringSQL(sb.append("CAST("), this.value).append(" AS CHAR(").append(length > 0 ? length : 1).append("))");
        }
        return StringUtils.quoteStringSQL(sb, this.value);
    }

    public static ValueChar get(String str) {
        ValueChar valueChar = new ValueChar(StringUtils.cache(str));
        if (str.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueChar;
        }
        return (ValueChar) Value.cache(valueChar);
    }
}
