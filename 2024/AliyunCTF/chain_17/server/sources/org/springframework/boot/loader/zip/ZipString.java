package org.springframework.boot.loader.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.jooq.types.UShort;
import org.springframework.asm.Opcodes;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: server.jar:org/springframework/boot/loader/zip/ZipString.class */
final class ZipString {
    static final int BUFFER_SIZE = 256;
    private static final int SUBSEQUENT_BYTE_BITMASK = 63;
    private static final DebugLogger debug = DebugLogger.get(ZipString.class);
    private static final int[] INITIAL_BYTE_BITMASK = {Opcodes.LAND, 31, 15, 7};
    private static final int EMPTY_HASH = "".hashCode();
    private static final int EMPTY_SLASH_HASH = "/".hashCode();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/zip/ZipString$CompareType.class */
    public enum CompareType {
        MATCHES,
        MATCHES_ADDING_SLASH,
        STARTS_WITH
    }

    private ZipString() {
    }

    static int hash(CharSequence charSequence, boolean addEndSlash) {
        return hash(0, charSequence, addEndSlash);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int hash(int initialHash, CharSequence charSequence, boolean addEndSlash) {
        if (charSequence == null || charSequence.isEmpty()) {
            return !addEndSlash ? EMPTY_HASH : EMPTY_SLASH_HASH;
        }
        boolean endsWithSlash = charSequence.charAt(charSequence.length() - 1) == '/';
        int hash = initialHash;
        if ((charSequence instanceof String) && initialHash == 0) {
            hash = charSequence.hashCode();
        } else {
            for (int i = 0; i < charSequence.length(); i++) {
                char ch2 = charSequence.charAt(i);
                hash = (31 * hash) + ch2;
            }
        }
        int hash2 = (!addEndSlash || endsWithSlash) ? hash : (31 * hash) + 47;
        debug.log("%s calculated for charsequence '%s' (addEndSlash=%s)", Integer.valueOf(hash2), charSequence, Boolean.valueOf(endsWithSlash));
        return hash2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int hash(ByteBuffer buffer, DataBlock dataBlock, long pos, int len, boolean addEndSlash) throws IOException {
        int highSurrogate;
        char lowSurrogate;
        if (len == 0) {
            return !addEndSlash ? EMPTY_HASH : EMPTY_SLASH_HASH;
        }
        ByteBuffer buffer2 = buffer != null ? buffer : ByteBuffer.allocate(256);
        byte[] bytes = buffer2.array();
        int hash = 0;
        char lastChar = 0;
        int codePointSize = 1;
        while (len > 0) {
            int count = readInBuffer(dataBlock, pos, buffer2, len, codePointSize);
            int byteIndex = 0;
            while (byteIndex < count) {
                codePointSize = getCodePointSize(bytes, byteIndex);
                if (!hasEnoughBytes(byteIndex, codePointSize, count)) {
                    break;
                }
                int codePoint = getCodePoint(bytes, byteIndex, codePointSize);
                if (codePoint <= 65535) {
                    lastChar = (char) (codePoint & UShort.MAX_VALUE);
                    highSurrogate = 31 * hash;
                    lowSurrogate = lastChar;
                } else {
                    lastChar = 0;
                    highSurrogate = 31 * ((31 * hash) + Character.highSurrogate(codePoint));
                    lowSurrogate = Character.lowSurrogate(codePoint);
                }
                hash = highSurrogate + lowSurrogate;
                byteIndex += codePointSize;
                pos += codePointSize;
                len -= codePointSize;
                codePointSize = 1;
            }
        }
        int hash2 = (!addEndSlash || lastChar == '/') ? hash : (31 * hash) + 47;
        debug.log("%08X calculated for datablock position %s size %s (addEndSlash=%s)", Integer.valueOf(hash2), Long.valueOf(pos), Integer.valueOf(len), Boolean.valueOf(addEndSlash));
        return hash2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matches(ByteBuffer buffer, DataBlock dataBlock, long pos, int len, CharSequence charSequence, boolean addSlash) {
        if (charSequence.isEmpty()) {
            return true;
        }
        try {
            return compare(buffer != null ? buffer : ByteBuffer.allocate(256), dataBlock, pos, len, charSequence, !addSlash ? CompareType.MATCHES : CompareType.MATCHES_ADDING_SLASH) != -1;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int startsWith(ByteBuffer buffer, DataBlock dataBlock, long pos, int len, CharSequence charSequence) {
        if (charSequence.isEmpty()) {
            return 0;
        }
        try {
            return compare(buffer != null ? buffer : ByteBuffer.allocate(256), dataBlock, pos, len, charSequence, CompareType.STARTS_WITH);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:58:0x004e, code lost:            continue;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int compare(java.nio.ByteBuffer r7, org.springframework.boot.loader.zip.DataBlock r8, long r9, int r11, java.lang.CharSequence r12, org.springframework.boot.loader.zip.ZipString.CompareType r13) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 327
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.loader.zip.ZipString.compare(java.nio.ByteBuffer, org.springframework.boot.loader.zip.DataBlock, long, int, java.lang.CharSequence, org.springframework.boot.loader.zip.ZipString$CompareType):int");
    }

    private static boolean hasEnoughBytes(int byteIndex, int codePointSize, int count) {
        return (byteIndex + codePointSize) - 1 < count;
    }

    private static boolean endsWith(CharSequence charSequence, char ch2) {
        return !charSequence.isEmpty() && charSequence.charAt(charSequence.length() - 1) == ch2;
    }

    private static char getChar(CharSequence charSequence, int index) {
        if (index != charSequence.length()) {
            return charSequence.charAt(index);
        }
        return '/';
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readString(DataBlock data, long pos, long len) {
        try {
            if (len > 2147483647L) {
                throw new IllegalStateException("String is too long to read");
            }
            ByteBuffer buffer = ByteBuffer.allocate((int) len);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            data.readFully(buffer, pos);
            return new String(buffer.array(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static int readInBuffer(DataBlock dataBlock, long pos, ByteBuffer buffer, int maxLen, int minLen) throws IOException {
        buffer.clear();
        if (buffer.remaining() > maxLen) {
            buffer.limit(maxLen);
        }
        int result = 0;
        while (result < minLen) {
            int count = dataBlock.read(buffer, pos);
            if (count <= 0) {
                throw new EOFException();
            }
            result += count;
            pos += count;
        }
        return result;
    }

    private static int getCodePointSize(byte[] bytes, int i) {
        int b = Byte.toUnsignedInt(bytes[i]);
        if ((b & 128) == 0) {
            return 1;
        }
        if ((b & 224) == 192) {
            return 2;
        }
        if ((b & 240) == 224) {
            return 3;
        }
        return 4;
    }

    private static int getCodePoint(byte[] bytes, int i, int codePointSize) {
        int codePoint = Byte.toUnsignedInt(bytes[i]);
        int codePoint2 = codePoint & INITIAL_BYTE_BITMASK[codePointSize - 1];
        for (int j = 1; j < codePointSize; j++) {
            codePoint2 = (codePoint2 << 6) + (bytes[i + j] & 63);
        }
        return codePoint2;
    }
}
