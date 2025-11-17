package org.h2.value;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SysProperties;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueVarbinary.class */
public final class ValueVarbinary extends ValueBytesBase {
    public static final ValueVarbinary EMPTY = new ValueVarbinary(Utils.EMPTY_BYTES);
    private TypeInfo type;

    @Override // org.h2.value.ValueBytesBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int getMemory() {
        return super.getMemory();
    }

    @Override // org.h2.value.ValueBytesBase, org.h2.util.HasSQL
    public /* bridge */ /* synthetic */ StringBuilder getSQL(StringBuilder sb, int i) {
        return super.getSQL(sb, i);
    }

    private ValueVarbinary(byte[] bArr) {
        super(bArr);
    }

    public static ValueVarbinary get(byte[] bArr) {
        if (bArr.length == 0) {
            return EMPTY;
        }
        return getNoCopy(Utils.cloneByteArray(bArr));
    }

    public static ValueVarbinary getNoCopy(byte[] bArr) {
        if (bArr.length == 0) {
            return EMPTY;
        }
        ValueVarbinary valueVarbinary = new ValueVarbinary(bArr);
        if (bArr.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueVarbinary;
        }
        return (ValueVarbinary) Value.cache(valueVarbinary);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = new TypeInfo(6, this.value.length, 0, null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 6;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return new String(this.value, StandardCharsets.UTF_8);
    }
}
