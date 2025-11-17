package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueJavaObject.class */
public final class ValueJavaObject extends ValueBytesBase {
    private static final ValueJavaObject EMPTY = new ValueJavaObject(Utils.EMPTY_BYTES);

    @Override // org.h2.value.ValueBytesBase, org.h2.value.Value
    public /* bridge */ /* synthetic */ int getMemory() {
        return super.getMemory();
    }

    private ValueJavaObject(byte[] bArr) {
        super(bArr);
    }

    public static ValueJavaObject getNoCopy(byte[] bArr) {
        int length = bArr.length;
        if (length == 0) {
            return EMPTY;
        }
        ValueJavaObject valueJavaObject = new ValueJavaObject(bArr);
        if (length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return valueJavaObject;
        }
        return (ValueJavaObject) Value.cache(valueJavaObject);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_JAVA_OBJECT;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 35;
    }

    @Override // org.h2.value.ValueBytesBase, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return super.getSQL(sb.append("CAST("), 0).append(" AS JAVA_OBJECT)");
        }
        return super.getSQL(sb, 0);
    }

    @Override // org.h2.value.Value
    public String getString() {
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "JAVA_OBJECT to CHARACTER VARYING");
    }
}
