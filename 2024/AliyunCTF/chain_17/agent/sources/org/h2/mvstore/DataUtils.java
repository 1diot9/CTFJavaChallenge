package org.h2.mvstore;

import cn.hutool.core.net.NetUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.h2.jdbc.JdbcException;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/DataUtils.class */
public final class DataUtils {
    public static final int ERROR_READING_FAILED = 1;
    public static final int ERROR_WRITING_FAILED = 2;
    public static final int ERROR_INTERNAL = 3;
    public static final int ERROR_CLOSED = 4;
    public static final int ERROR_UNSUPPORTED_FORMAT = 5;
    public static final int ERROR_FILE_CORRUPT = 6;
    public static final int ERROR_FILE_LOCKED = 7;
    public static final int ERROR_SERIALIZATION = 8;
    public static final int ERROR_CHUNK_NOT_FOUND = 9;
    public static final int ERROR_BLOCK_NOT_FOUND = 50;
    public static final int ERROR_TRANSACTION_CORRUPT = 100;
    public static final int ERROR_TRANSACTION_LOCKED = 101;
    public static final int ERROR_TOO_MANY_OPEN_TRANSACTIONS = 102;
    public static final int ERROR_TRANSACTION_ILLEGAL_STATE = 103;
    public static final int ERROR_TRANSACTION_TOO_BIG = 104;
    public static final int ERROR_TRANSACTIONS_DEADLOCK = 105;
    public static final int ERROR_UNKNOWN_DATA_TYPE = 106;
    public static final int PAGE_TYPE_LEAF = 0;
    public static final int PAGE_TYPE_NODE = 1;
    public static final int PAGE_COMPRESSED = 2;
    public static final int PAGE_COMPRESSED_HIGH = 6;
    public static final int COMPRESSED_VAR_INT_MAX = 2097151;
    public static final long COMPRESSED_VAR_LONG_MAX = 562949953421311L;
    public static final int PAGE_LARGE = 2097152;
    public static final String META_CHUNK = "chunk.";
    public static final String META_ROOT = "root.";
    public static final String META_NAME = "name.";
    public static final String META_MAP = "map.";
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !DataUtils.class.desiredAssertionStatus();
    }

    public static int getVarIntLen(int i) {
        if ((i & (-128)) == 0) {
            return 1;
        }
        if ((i & (-16384)) == 0) {
            return 2;
        }
        if ((i & (-2097152)) == 0) {
            return 3;
        }
        if ((i & (-268435456)) == 0) {
            return 4;
        }
        return 5;
    }

    public static int getVarLongLen(long j) {
        int i = 1;
        while (true) {
            j >>>= 7;
            if (j == 0) {
                return i;
            }
            i++;
        }
    }

    public static int readVarInt(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        if (b >= 0) {
            return b;
        }
        return readVarIntRest(byteBuffer, b);
    }

    private static int readVarIntRest(ByteBuffer byteBuffer, int i) {
        int i2 = i & 127;
        byte b = byteBuffer.get();
        if (b >= 0) {
            return i2 | (b << 7);
        }
        int i3 = i2 | ((b & Byte.MAX_VALUE) << 7);
        byte b2 = byteBuffer.get();
        if (b2 >= 0) {
            return i3 | (b2 << 14);
        }
        int i4 = i3 | ((b2 & Byte.MAX_VALUE) << 14);
        byte b3 = byteBuffer.get();
        if (b3 >= 0) {
            return i4 | (b3 << 21);
        }
        return i4 | ((b3 & Byte.MAX_VALUE) << 21) | (byteBuffer.get() << 28);
    }

    public static long readVarLong(ByteBuffer byteBuffer) {
        long j = byteBuffer.get();
        if (j >= 0) {
            return j;
        }
        long j2 = j & 127;
        for (int i = 7; i < 64; i += 7) {
            long j3 = byteBuffer.get();
            j2 |= (j3 & 127) << i;
            if (j3 >= 0) {
                break;
            }
        }
        return j2;
    }

    public static void writeVarInt(OutputStream outputStream, int i) throws IOException {
        while ((i & (-128)) != 0) {
            outputStream.write((byte) (i | 128));
            i >>>= 7;
        }
        outputStream.write((byte) i);
    }

    public static void writeVarInt(ByteBuffer byteBuffer, int i) {
        while ((i & (-128)) != 0) {
            byteBuffer.put((byte) (i | 128));
            i >>>= 7;
        }
        byteBuffer.put((byte) i);
    }

    public static void writeStringData(ByteBuffer byteBuffer, String str, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            char charAt = str.charAt(i2);
            if (charAt < 128) {
                byteBuffer.put((byte) charAt);
            } else if (charAt >= 2048) {
                byteBuffer.put((byte) (224 | (charAt >> '\f')));
                byteBuffer.put((byte) ((charAt >> 6) & 63));
                byteBuffer.put((byte) (charAt & '?'));
            } else {
                byteBuffer.put((byte) (192 | (charAt >> 6)));
                byteBuffer.put((byte) (charAt & '?'));
            }
        }
    }

    public static String readString(ByteBuffer byteBuffer) {
        return readString(byteBuffer, readVarInt(byteBuffer));
    }

    public static String readString(ByteBuffer byteBuffer, int i) {
        char[] cArr = new char[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = byteBuffer.get() & 255;
            if (i3 < 128) {
                cArr[i2] = (char) i3;
            } else if (i3 >= 224) {
                cArr[i2] = (char) (((i3 & 15) << 12) + ((byteBuffer.get() & 63) << 6) + (byteBuffer.get() & 63));
            } else {
                cArr[i2] = (char) (((i3 & 31) << 6) + (byteBuffer.get() & 63));
            }
        }
        return new String(cArr);
    }

    public static void writeVarLong(ByteBuffer byteBuffer, long j) {
        while ((j & (-128)) != 0) {
            byteBuffer.put((byte) (j | 128));
            j >>>= 7;
        }
        byteBuffer.put((byte) j);
    }

    public static void writeVarLong(OutputStream outputStream, long j) throws IOException {
        while ((j & (-128)) != 0) {
            outputStream.write((byte) (j | 128));
            j >>>= 7;
        }
        outputStream.write((byte) j);
    }

    public static void copyWithGap(Object obj, Object obj2, int i, int i2) {
        if (i2 > 0) {
            System.arraycopy(obj, 0, obj2, 0, i2);
        }
        if (i2 < i) {
            System.arraycopy(obj, i2, obj2, i2 + 1, i - i2);
        }
    }

    public static void copyExcept(Object obj, Object obj2, int i, int i2) {
        if (i2 > 0 && i > 0) {
            System.arraycopy(obj, 0, obj2, 0, i2);
        }
        if (i2 < i) {
            System.arraycopy(obj, i2 + 1, obj2, i2, (i - i2) - 1);
        }
    }

    public static void readFully(FileChannel fileChannel, long j, ByteBuffer byteBuffer) {
        long j2;
        do {
            try {
                int read = fileChannel.read(byteBuffer, j);
                if (read < 0) {
                    throw new EOFException();
                }
                j += read;
            } catch (IOException e) {
                try {
                    j2 = fileChannel.size();
                } catch (IOException e2) {
                    j2 = -1;
                }
                throw newMVStoreException(1, "Reading from file {0} failed at {1} (length {2}), read {3}, remaining {4}", fileChannel, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(byteBuffer.position()), Integer.valueOf(byteBuffer.remaining()), e);
            }
        } while (byteBuffer.remaining() > 0);
        byteBuffer.rewind();
    }

    public static void writeFully(FileChannel fileChannel, long j, ByteBuffer byteBuffer) {
        int i = 0;
        do {
            try {
                i += fileChannel.write(byteBuffer, j + i);
            } catch (IOException e) {
                throw newMVStoreException(2, "Writing to {0} failed; length {1} at {2}", fileChannel, Integer.valueOf(byteBuffer.remaining()), Long.valueOf(j), e);
            }
        } while (byteBuffer.remaining() > 0);
    }

    public static int encodeLength(int i) {
        if (!$assertionsDisabled && i < 0) {
            throw new AssertionError();
        }
        if (i <= 32) {
            return 0;
        }
        int numberOfLeadingZeros = Integer.numberOfLeadingZeros(i);
        int i2 = i << (numberOfLeadingZeros + 1);
        int i3 = numberOfLeadingZeros + numberOfLeadingZeros;
        if ((i2 & Integer.MIN_VALUE) != 0) {
            i3--;
        }
        if ((i2 << 1) != 0) {
            i3--;
        }
        return Math.min(31, 52 - i3);
    }

    public static int getPageChunkId(long j) {
        return (int) (j >>> 38);
    }

    public static int getPageMapId(long j) {
        return (int) (j >>> 38);
    }

    public static int getPageMaxLength(long j) {
        return decodePageLength((int) ((j >> 1) & 31));
    }

    public static int decodePageLength(int i) {
        if (i == 31) {
            return 2097152;
        }
        return (2 + (i & 1)) << ((i >> 1) + 4);
    }

    public static int getPageOffset(long j) {
        return (int) (j >> 6);
    }

    public static int getPageType(long j) {
        return ((int) j) & 1;
    }

    public static boolean isLeafPosition(long j) {
        return getPageType(j) == 0;
    }

    public static boolean isPageSaved(long j) {
        return (j & (-2)) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPageRemoved(long j) {
        return j == 1;
    }

    public static long composePagePos(int i, int i2, int i3, int i4) {
        if (!$assertionsDisabled && i2 < 0) {
            throw new AssertionError();
        }
        if ($assertionsDisabled || i4 == 0 || i4 == 1) {
            return (i << 38) | (i2 << 6) | (encodeLength(i3) << 1) | i4;
        }
        throw new AssertionError();
    }

    public static long composePagePos(int i, long j) {
        return (j & 274877906943L) | (i << 38);
    }

    public static long composeTocElement(int i, int i2, int i3, int i4) {
        if (!$assertionsDisabled && i < 0) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && i2 < 0) {
            throw new AssertionError();
        }
        if ($assertionsDisabled || i4 == 0 || i4 == 1) {
            return (i << 38) | (i2 << 6) | (encodeLength(i3) << 1) | i4;
        }
        throw new AssertionError();
    }

    public static short getCheckValue(int i) {
        return (short) ((i >> 16) ^ i);
    }

    public static StringBuilder appendMap(StringBuilder sb, HashMap<String, ?> hashMap) {
        Object[] array = hashMap.keySet().toArray();
        Arrays.sort(array);
        for (Object obj : array) {
            String str = (String) obj;
            Object obj2 = hashMap.get(str);
            if (obj2 instanceof Long) {
                appendMap(sb, str, ((Long) obj2).longValue());
            } else if (obj2 instanceof Integer) {
                appendMap(sb, str, ((Integer) obj2).intValue());
            } else {
                appendMap(sb, str, obj2.toString());
            }
        }
        return sb;
    }

    private static StringBuilder appendMapKey(StringBuilder sb, String str) {
        if (sb.length() > 0) {
            sb.append(',');
        }
        return sb.append(str).append(':');
    }

    public static void appendMap(StringBuilder sb, String str, String str2) {
        appendMapKey(sb, str);
        if (str2.indexOf(44) < 0 && str2.indexOf(34) < 0) {
            sb.append(str2);
            return;
        }
        sb.append('\"');
        int length = str2.length();
        for (int i = 0; i < length; i++) {
            char charAt = str2.charAt(i);
            if (charAt == '\"') {
                sb.append('\\');
            }
            sb.append(charAt);
        }
        sb.append('\"');
    }

    public static void appendMap(StringBuilder sb, String str, long j) {
        appendMapKey(sb, str).append(Long.toHexString(j));
    }

    public static void appendMap(StringBuilder sb, String str, int i) {
        appendMapKey(sb, str).append(Integer.toHexString(i));
    }

    private static int parseMapValue(StringBuilder sb, String str, int i, int i2) {
        while (i < i2) {
            int i3 = i;
            i++;
            char charAt = str.charAt(i3);
            if (charAt == ',') {
                break;
            }
            if (charAt == '\"') {
                while (i < i2) {
                    int i4 = i;
                    i++;
                    char charAt2 = str.charAt(i4);
                    if (charAt2 == '\\') {
                        if (i == i2) {
                            throw newMVStoreException(6, "Not a map: {0}", str);
                        }
                        i++;
                        charAt2 = str.charAt(i);
                    } else if (charAt2 == '\"') {
                        break;
                    }
                    sb.append(charAt2);
                }
            } else {
                sb.append(charAt);
            }
        }
        return i;
    }

    public static HashMap<String, String> parseMap(String str) {
        HashMap<String, String> hashMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int length = str.length();
        while (i < length) {
            int i2 = i;
            int indexOf = str.indexOf(58, i);
            if (indexOf < 0) {
                throw newMVStoreException(6, "Not a map: {0}", str);
            }
            int i3 = indexOf + 1;
            String substring = str.substring(i2, indexOf);
            i = parseMapValue(sb, str, i3, length);
            hashMap.put(substring, sb.toString());
            sb.setLength(0);
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashMap<String, String> parseChecksummedMap(byte[] bArr) {
        int i = 0;
        int length = bArr.length;
        while (i < length && bArr[i] <= 32) {
            i++;
        }
        while (i < length && bArr[length - 1] <= 32) {
            length--;
        }
        String str = new String(bArr, i, length - i, StandardCharsets.ISO_8859_1);
        HashMap<String, String> hashMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        int length2 = str.length();
        while (i2 < length2) {
            int i3 = i2;
            int indexOf = str.indexOf(58, i2);
            if (indexOf < 0) {
                return null;
            }
            if (indexOf - i3 == 8 && str.regionMatches(i3, "fletcher", 0, 8)) {
                parseMapValue(sb, str, indexOf + 1, length2);
                if (((int) Long.parseLong(sb.toString(), 16)) == getFletcher32(bArr, i, i3 - 1)) {
                    return hashMap;
                }
                return null;
            }
            int i4 = indexOf + 1;
            String substring = str.substring(i3, indexOf);
            i2 = parseMapValue(sb, str, i4, length2);
            hashMap.put(substring, sb.toString());
            sb.setLength(0);
        }
        return null;
    }

    public static String getMapName(String str) {
        return getFromMap(str, "name");
    }

    /* JADX WARN: Code restructure failed: missing block: B:42:0x000d, code lost:            continue;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String getFromMap(java.lang.String r7, java.lang.String r8) {
        /*
            Method dump skipped, instructions count: 197
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.DataUtils.getFromMap(java.lang.String, java.lang.String):java.lang.String");
    }

    public static int getFletcher32(byte[] bArr, int i, int i2) {
        int i3 = 65535;
        int i4 = 65535;
        int i5 = i;
        int i6 = i + (i2 & (-2));
        while (i5 < i6) {
            int min = Math.min(i5 + 720, i6);
            while (i5 < min) {
                int i7 = i5;
                int i8 = i5 + 1;
                i5 = i8 + 1;
                int i9 = i3 + (((bArr[i7] & 255) << 8) | (bArr[i8] & 255));
                i3 = i9;
                i4 += i9;
            }
            i3 = (i3 & NetUtil.PORT_RANGE_MAX) + (i3 >>> 16);
            i4 = (i4 & NetUtil.PORT_RANGE_MAX) + (i4 >>> 16);
        }
        if ((i2 & 1) != 0) {
            int i10 = i3 + ((bArr[i5] & 255) << 8);
            i3 = i10;
            i4 += i10;
        }
        return (((i4 & NetUtil.PORT_RANGE_MAX) + (i4 >>> 16)) << 16) | ((i3 & NetUtil.PORT_RANGE_MAX) + (i3 >>> 16));
    }

    public static void checkArgument(boolean z, String str, Object... objArr) {
        if (!z) {
            throw newIllegalArgumentException(str, objArr);
        }
    }

    public static IllegalArgumentException newIllegalArgumentException(String str, Object... objArr) {
        return (IllegalArgumentException) initCause(new IllegalArgumentException(formatMessage(0, str, objArr)), objArr);
    }

    public static UnsupportedOperationException newUnsupportedOperationException(String str) {
        return new UnsupportedOperationException(formatMessage(0, str, new Object[0]));
    }

    public static MVStoreException newMVStoreException(int i, String str, Object... objArr) {
        return (MVStoreException) initCause(new MVStoreException(i, formatMessage(i, str, objArr)), objArr);
    }

    private static <T extends Exception> T initCause(T t, Object... objArr) {
        int length = objArr.length;
        if (length > 0) {
            Object obj = objArr[length - 1];
            if (obj instanceof Throwable) {
                t.initCause((Throwable) obj);
            }
        }
        return t;
    }

    public static String formatMessage(int i, String str, Object... objArr) {
        Object[] objArr2 = (Object[]) objArr.clone();
        for (int i2 = 0; i2 < objArr2.length; i2++) {
            Object obj = objArr2[i2];
            if (!(obj instanceof Exception)) {
                String obj2 = obj == null ? "null" : obj.toString();
                if (obj2.length() > 1000) {
                    obj2 = obj2.substring(0, 1000) + "...";
                }
                objArr2[i2] = obj2;
            }
        }
        return MessageFormat.format(str, objArr2) + " [2.2.224/" + i + "]";
    }

    public static long readHexLong(Map<String, ?> map, String str, long j) {
        Object obj = map.get(str);
        if (obj == null) {
            return j;
        }
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        try {
            return parseHexLong((String) obj);
        } catch (NumberFormatException e) {
            throw newMVStoreException(6, "Error parsing the value {0}", obj, e);
        }
    }

    public static long parseHexLong(String str) {
        try {
            if (str.length() == 16) {
                return (Long.parseLong(str.substring(0, 8), 16) << 32) | Long.parseLong(str.substring(8, 16), 16);
            }
            return Long.parseLong(str, 16);
        } catch (NumberFormatException e) {
            throw newMVStoreException(6, "Error parsing the value {0}", str, e);
        }
    }

    public static int parseHexInt(String str) {
        try {
            return (int) Long.parseLong(str, 16);
        } catch (NumberFormatException e) {
            throw newMVStoreException(6, "Error parsing the value {0}", str, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int readHexInt(Map<String, ?> map, String str, int i) {
        Object obj = map.get(str);
        if (obj == null) {
            return i;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        try {
            return (int) Long.parseLong((String) obj, 16);
        } catch (NumberFormatException e) {
            throw newMVStoreException(6, "Error parsing the value {0}", obj, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] parseHexBytes(Map<String, ?> map, String str) {
        Object obj = map.get(str);
        if (obj == null) {
            return null;
        }
        return StringUtils.convertHexToBytes((String) obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getConfigParam(Map<String, ?> map, String str, int i) {
        Object obj = map.get(str);
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj != null) {
            try {
                return Integer.decode(obj.toString()).intValue();
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public static IOException convertToIOException(Throwable th) {
        if (th instanceof IOException) {
            return (IOException) th;
        }
        if ((th instanceof JdbcException) && th.getCause() != null) {
            th = th.getCause();
        }
        return new IOException(th.toString(), th);
    }
}
