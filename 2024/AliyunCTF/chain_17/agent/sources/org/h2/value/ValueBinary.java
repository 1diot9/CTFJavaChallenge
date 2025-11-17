package org.h2.value;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SysProperties;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBinary.class */
public final class ValueBinary extends ValueBytesBase {
    private TypeInfo type;

    @Override // org.h2.value.ValueBytesBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int getMemory() {
        return super.getMemory();
    }

    private ValueBinary(byte[] bArr) {
        super(bArr);
    }

    public static ValueBinary get(byte[] bArr) {
        return getNoCopy(Utils.cloneByteArray(bArr));
    }

    public static ValueBinary getNoCopy(byte[] bArr) {
        ValueBinary valueBinary = new ValueBinary(bArr);
        if (bArr.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueBinary;
        }
        return (ValueBinary) Value.cache(valueBinary);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = new TypeInfo(5, this.value.length, 0, null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 5;
    }

    @Override // org.h2.value.ValueBytesBase, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            int length = this.value.length;
            return super.getSQL(sb.append("CAST("), i).append(" AS BINARY(").append(length > 0 ? length : 1).append("))");
        }
        return super.getSQL(sb, i);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return new String(this.value, StandardCharsets.UTF_8);
    }
}
