package org.h2.value;

import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueEnum.class */
public final class ValueEnum extends ValueEnumBase {
    private final ExtTypeInfoEnum enumerators;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueEnum(ExtTypeInfoEnum extTypeInfoEnum, String str, int i) {
        super(str, i);
        this.enumerators = extTypeInfoEnum;
    }

    @Override // org.h2.value.ValueEnumBase, org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return this.enumerators.getType();
    }

    public ExtTypeInfoEnum getEnumerators() {
        return this.enumerators;
    }

    @Override // org.h2.value.ValueEnumBase, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            StringUtils.quoteStringSQL(sb.append("CAST("), this.label).append(" AS ");
            return this.enumerators.getType().getSQL(sb, i).append(')');
        }
        return StringUtils.quoteStringSQL(sb, this.label);
    }
}
