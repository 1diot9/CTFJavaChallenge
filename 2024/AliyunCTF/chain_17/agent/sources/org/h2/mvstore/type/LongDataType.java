package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/LongDataType.class */
public class LongDataType extends BasicDataType<Long> {
    public static final LongDataType INSTANCE = new LongDataType();
    private static final Long[] EMPTY_LONG_ARR = new Long[0];

    private LongDataType() {
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(Long l) {
        return 8;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Long l) {
        writeBuffer.putVarLong(l.longValue());
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public Long read(ByteBuffer byteBuffer) {
        return Long.valueOf(DataUtils.readVarLong(byteBuffer));
    }

    @Override // org.h2.mvstore.type.DataType
    public Long[] createStorage(int i) {
        return i == 0 ? EMPTY_LONG_ARR : new Long[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(Long l, Long l2) {
        return Long.compare(l.longValue(), l2.longValue());
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int binarySearch(Long l, Object obj, int i, int i2) {
        long longValue = l.longValue();
        Long[] cast = cast(obj);
        int i3 = i - 1;
        int i4 = i2 - 1;
        if (i4 < 0 || i4 > i3) {
            i4 = i3 >>> 1;
        }
        return binarySearch(longValue, cast, 0, i3, i4);
    }

    private static int binarySearch(long j, Long[] lArr, int i, int i2, int i3) {
        while (i <= i2) {
            long longValue = lArr[i3].longValue();
            if (j > longValue) {
                i = i3 + 1;
            } else if (j < longValue) {
                i2 = i3 - 1;
            } else {
                return i3;
            }
            i3 = (i + i2) >>> 1;
        }
        return -(i + 1);
    }
}
