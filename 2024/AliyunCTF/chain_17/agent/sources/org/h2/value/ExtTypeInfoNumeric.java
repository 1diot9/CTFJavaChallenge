package org.h2.value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ExtTypeInfoNumeric.class */
public final class ExtTypeInfoNumeric extends ExtTypeInfo {
    public static final ExtTypeInfoNumeric DECIMAL = new ExtTypeInfoNumeric();

    private ExtTypeInfoNumeric() {
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append("DECIMAL");
    }
}
