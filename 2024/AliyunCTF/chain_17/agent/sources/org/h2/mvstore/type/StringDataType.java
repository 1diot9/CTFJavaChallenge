package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/StringDataType.class */
public class StringDataType extends BasicDataType<String> {
    public static final StringDataType INSTANCE = new StringDataType();
    private static final String[] EMPTY_STRING_ARR = new String[0];

    @Override // org.h2.mvstore.type.DataType
    public String[] createStorage(int i) {
        return i == 0 ? EMPTY_STRING_ARR : new String[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(String str, String str2) {
        return str.compareTo(str2);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int binarySearch(String str, Object obj, int i, int i2) {
        String[] cast = cast(obj);
        int i3 = 0;
        int i4 = i - 1;
        int i5 = i2 - 1;
        if (i5 < 0 || i5 > i4) {
            i5 = i4 >>> 1;
        }
        while (i3 <= i4) {
            int compareTo = str.compareTo(cast[i5]);
            if (compareTo > 0) {
                i3 = i5 + 1;
            } else if (compareTo < 0) {
                i4 = i5 - 1;
            } else {
                return i5;
            }
            i5 = (i3 + i4) >>> 1;
        }
        return -(i3 + 1);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(String str) {
        return 24 + (2 * str.length());
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public String read(ByteBuffer byteBuffer) {
        return DataUtils.readString(byteBuffer);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, String str) {
        int length = str.length();
        writeBuffer.putVarInt(length).putStringData(str, length);
    }
}
