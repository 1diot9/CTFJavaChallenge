package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueVarchar.class */
public final class ValueVarchar extends ValueStringBase {
    public static final ValueVarchar EMPTY = new ValueVarchar("");

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.h2.value.ValueStringBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return super.compareTypeSafe(value, compareMode, castDataProvider);
    }

    private ValueVarchar(String str) {
        super(str);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return StringUtils.quoteStringSQL(sb, this.value);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 2;
    }

    public static Value get(String str) {
        return get(str, null);
    }

    public static Value get(String str, CastDataProvider castDataProvider) {
        if (str.isEmpty()) {
            return (castDataProvider == null || !castDataProvider.getMode().treatEmptyStringsAsNull) ? EMPTY : ValueNull.INSTANCE;
        }
        ValueVarchar valueVarchar = new ValueVarchar(StringUtils.cache(str));
        if (str.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueVarchar;
        }
        return Value.cache(valueVarchar);
    }
}
