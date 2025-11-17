package org.h2.value;

import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBytesBase.class */
public abstract class ValueBytesBase extends Value {
    byte[] value;
    int hash;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueBytesBase(byte[] bArr) {
        int length = bArr.length;
        if (length > 1000000000) {
            throw DbException.getValueTooLongException(getTypeName(getValueType()), StringUtils.convertBytesToHex(bArr, 41), length);
        }
        this.value = bArr;
    }

    @Override // org.h2.value.Value
    public final byte[] getBytes() {
        return Utils.cloneByteArray(this.value);
    }

    @Override // org.h2.value.Value
    public final byte[] getBytesNoCopy() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public final int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Bits.compareNotNullUnsigned(this.value, ((ValueBytesBase) value).value);
    }

    public StringBuilder getSQL(StringBuilder sb, int i) {
        return StringUtils.convertBytesToHex(sb.append("X'"), this.value).append('\'');
    }

    @Override // org.h2.value.Value
    public final int hashCode() {
        int i = this.hash;
        if (i == 0) {
            i = getClass().hashCode() ^ Utils.getByteArrayHash(this.value);
            if (i == 0) {
                i = 1234570417;
            }
            this.hash = i;
        }
        return i;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return this.value.length + 24;
    }

    @Override // org.h2.value.Value
    public final boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && Arrays.equals(this.value, ((ValueBytesBase) obj).value);
    }
}
