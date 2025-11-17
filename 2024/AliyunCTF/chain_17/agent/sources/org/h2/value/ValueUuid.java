package org.h2.value;

import java.util.UUID;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueUuid.class */
public final class ValueUuid extends Value {
    static final int PRECISION = 16;
    static final int DISPLAY_SIZE = 36;
    private final long high;
    private final long low;

    private ValueUuid(long j, long j2) {
        this.high = j;
        this.low = j2;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) ((((this.high >>> 32) ^ this.high) ^ (this.low >>> 32)) ^ this.low);
    }

    public static ValueUuid getNewRandom() {
        return new ValueUuid((MathUtils.secureRandomLong() & (-61441)) | 16384, (MathUtils.secureRandomLong() & 4611686018427387903L) | Long.MIN_VALUE);
    }

    public static ValueUuid get(byte[] bArr) {
        int length = bArr.length;
        if (length != 16) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "UUID requires 16 bytes, got " + length);
        }
        return get(Bits.readLong(bArr, 0), Bits.readLong(bArr, 8));
    }

    public static ValueUuid get(long j, long j2) {
        return (ValueUuid) Value.cache(new ValueUuid(j, j2));
    }

    public static ValueUuid get(UUID uuid) {
        return get(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static ValueUuid get(String str) {
        long j = 0;
        long j2 = 0;
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            if (charAt >= '0' && charAt <= '9') {
                j = (j << 4) | (charAt - '0');
            } else if (charAt >= 'a' && charAt <= 'f') {
                j = (j << 4) | (charAt - 'W');
            } else {
                if (charAt == '-') {
                    continue;
                } else if (charAt >= 'A' && charAt <= 'F') {
                    j = (j << 4) | (charAt - '7');
                } else if (charAt > ' ') {
                    throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, str);
                }
            }
            i++;
            if (i == 16) {
                j2 = j;
                j = 0;
            }
        }
        if (i != 32) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, str);
        }
        return get(j2, j);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return addString(sb.append("UUID '")).append('\'');
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_UUID;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 32;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 39;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return addString(new StringBuilder(36)).toString();
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        return Bits.uuidToBytes(this.high, this.low);
    }

    private StringBuilder addString(StringBuilder sb) {
        StringUtils.appendHex(sb, this.high >> 32, 4).append('-');
        StringUtils.appendHex(sb, this.high >> 16, 2).append('-');
        StringUtils.appendHex(sb, this.high, 2).append('-');
        StringUtils.appendHex(sb, this.low >> 48, 2).append('-');
        return StringUtils.appendHex(sb, this.low, 6);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        if (value == this) {
            return 0;
        }
        ValueUuid valueUuid = (ValueUuid) value;
        int compareUnsigned = Long.compareUnsigned(this.high, valueUuid.high);
        return compareUnsigned != 0 ? compareUnsigned : Long.compareUnsigned(this.low, valueUuid.low);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueUuid)) {
            return false;
        }
        ValueUuid valueUuid = (ValueUuid) obj;
        return this.high == valueUuid.high && this.low == valueUuid.low;
    }

    public UUID getUuid() {
        return new UUID(this.high, this.low);
    }

    public long getHigh() {
        return this.high;
    }

    public long getLow() {
        return this.low;
    }

    @Override // org.h2.value.Value
    public long charLength() {
        return 36L;
    }

    @Override // org.h2.value.Value
    public long octetLength() {
        return 16L;
    }
}
