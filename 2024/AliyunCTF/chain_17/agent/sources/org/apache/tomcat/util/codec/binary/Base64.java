package org.apache.tomcat.util.codec.binary;

import org.apache.tomcat.util.bcel.Const;
import org.apache.tomcat.util.codec.binary.BaseNCodec;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/codec/binary/Base64.class */
public class Base64 extends BaseNCodec {
    private static final int BITS_PER_ENCODED_BYTE = 6;
    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    private static final int BYTES_PER_ENCODED_BLOCK = 4;
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] STANDARD_DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
    private static final byte[] URL_SAFE_DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
    private static final int MASK_6BITS = 63;
    private static final int MASK_4BITS = 15;
    private static final int MASK_2BITS = 3;
    private final byte[] encodeTable;
    private final byte[] decodeTable;
    private final byte[] lineSeparator;
    private final int decodeSize;
    private final int encodeSize;

    public static byte[] decodeBase64(byte[] base64Data, int off, int len) {
        return new Base64().decode(base64Data, off, len);
    }

    public static byte[] decodeBase64(String base64String) {
        return new Base64().decode(base64String);
    }

    public static byte[] decodeBase64URLSafe(String base64String) {
        return new Base64(true).decode(base64String);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }
        Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
        long len = b64.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException(sm.getString("base64.inputTooLarge", Long.valueOf(len), Integer.valueOf(maxResultSize)));
        }
        return b64.encode(binaryData);
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newStringUsAscii(encodeBase64(binaryData, false));
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newStringUsAscii(encodeBase64(binaryData, false, true));
    }

    private static void validateCharacter(int emptyBitsMask, BaseNCodec.Context context) {
        if ((context.ibitWorkArea & emptyBitsMask) != 0) {
            throw new IllegalArgumentException("Last encoded character (before the paddings if any) is a valid base 64 alphabet but not a possible value. Expected the discarded bits to be zero.");
        }
    }

    public static boolean isInAlphabet(char c) {
        try {
            return STANDARD_DECODE_TABLE[c] != -1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public Base64() {
        this(0);
    }

    public Base64(boolean urlSafe) {
        this(76, CHUNK_SEPARATOR, urlSafe);
    }

    public Base64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        super(3, 4, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
        this.decodeTable = urlSafe ? URL_SAFE_DECODE_TABLE : STANDARD_DECODE_TABLE;
        if (lineSeparator != null) {
            if (containsAlphabetOrPad(lineSeparator)) {
                String sep = StringUtils.newStringUtf8(lineSeparator);
                throw new IllegalArgumentException(sm.getString("base64.lineSeparator", sep));
            }
            if (lineLength > 0) {
                this.encodeSize = 4 + lineSeparator.length;
                this.lineSeparator = (byte[]) lineSeparator.clone();
            } else {
                this.encodeSize = 4;
                this.lineSeparator = null;
            }
        } else {
            this.encodeSize = 4;
            this.lineSeparator = null;
        }
        this.decodeSize = this.encodeSize - 1;
        this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    @Override // org.apache.tomcat.util.codec.binary.BaseNCodec
    void decode(byte[] input, int inPos, int inAvail, BaseNCodec.Context context) {
        byte b;
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
        }
        int i = 0;
        while (true) {
            if (i >= inAvail) {
                break;
            }
            byte[] buffer = ensureBufferSize(this.decodeSize, context);
            int i2 = inPos;
            inPos++;
            byte b2 = input[i2];
            if (b2 == this.pad) {
                context.eof = true;
                break;
            }
            if (b2 >= 0 && b2 < this.decodeTable.length && (b = this.decodeTable[b2]) >= 0) {
                context.modulus = (context.modulus + 1) % 4;
                context.ibitWorkArea = (context.ibitWorkArea << 6) + b;
                if (context.modulus == 0) {
                    int i3 = context.pos;
                    context.pos = i3 + 1;
                    buffer[i3] = (byte) ((context.ibitWorkArea >> 16) & Const.MAX_ARRAY_DIMENSIONS);
                    int i4 = context.pos;
                    context.pos = i4 + 1;
                    buffer[i4] = (byte) ((context.ibitWorkArea >> 8) & Const.MAX_ARRAY_DIMENSIONS);
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = (byte) (context.ibitWorkArea & Const.MAX_ARRAY_DIMENSIONS);
                }
            }
            i++;
        }
        if (context.eof && context.modulus != 0) {
            byte[] buffer2 = ensureBufferSize(this.decodeSize, context);
            switch (context.modulus) {
                case 2:
                    validateCharacter(15, context);
                    context.ibitWorkArea >>= 4;
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer2[i6] = (byte) (context.ibitWorkArea & Const.MAX_ARRAY_DIMENSIONS);
                    return;
                case 3:
                    validateCharacter(3, context);
                    context.ibitWorkArea >>= 2;
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer2[i7] = (byte) ((context.ibitWorkArea >> 8) & Const.MAX_ARRAY_DIMENSIONS);
                    int i8 = context.pos;
                    context.pos = i8 + 1;
                    buffer2[i8] = (byte) (context.ibitWorkArea & Const.MAX_ARRAY_DIMENSIONS);
                    return;
                default:
                    throw new IllegalStateException(sm.getString("base64.impossibleModulus", Integer.valueOf(context.modulus)));
            }
        }
    }

    @Override // org.apache.tomcat.util.codec.binary.BaseNCodec
    void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
            if (0 == context.modulus && this.lineLength == 0) {
                return;
            }
            byte[] buffer = ensureBufferSize(this.encodeSize, context);
            int savedPos = context.pos;
            switch (context.modulus) {
                case 0:
                    break;
                case 1:
                    int i = context.pos;
                    context.pos = i + 1;
                    buffer[i] = this.encodeTable[(context.ibitWorkArea >> 2) & 63];
                    int i2 = context.pos;
                    context.pos = i2 + 1;
                    buffer[i2] = this.encodeTable[(context.ibitWorkArea << 4) & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        int i3 = context.pos;
                        context.pos = i3 + 1;
                        buffer[i3] = this.pad;
                        int i4 = context.pos;
                        context.pos = i4 + 1;
                        buffer[i4] = this.pad;
                        break;
                    }
                    break;
                case 2:
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = this.encodeTable[(context.ibitWorkArea >> 10) & 63];
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer[i6] = this.encodeTable[(context.ibitWorkArea >> 4) & 63];
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer[i7] = this.encodeTable[(context.ibitWorkArea << 2) & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        int i8 = context.pos;
                        context.pos = i8 + 1;
                        buffer[i8] = this.pad;
                        break;
                    }
                    break;
                default:
                    throw new IllegalStateException(sm.getString("base64.impossibleModulus", Integer.valueOf(context.modulus)));
            }
            context.currentLinePos += context.pos - savedPos;
            if (this.lineLength > 0 && context.currentLinePos > 0) {
                System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
                context.pos += this.lineSeparator.length;
                return;
            }
            return;
        }
        for (int i9 = 0; i9 < inAvail; i9++) {
            byte[] buffer2 = ensureBufferSize(this.encodeSize, context);
            context.modulus = (context.modulus + 1) % 3;
            int i10 = inPos;
            inPos++;
            int b = in[i10];
            if (b < 0) {
                b += 256;
            }
            context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
            if (0 == context.modulus) {
                int i11 = context.pos;
                context.pos = i11 + 1;
                buffer2[i11] = this.encodeTable[(context.ibitWorkArea >> 18) & 63];
                int i12 = context.pos;
                context.pos = i12 + 1;
                buffer2[i12] = this.encodeTable[(context.ibitWorkArea >> 12) & 63];
                int i13 = context.pos;
                context.pos = i13 + 1;
                buffer2[i13] = this.encodeTable[(context.ibitWorkArea >> 6) & 63];
                int i14 = context.pos;
                context.pos = i14 + 1;
                buffer2[i14] = this.encodeTable[context.ibitWorkArea & 63];
                context.currentLinePos += 4;
                if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
                    System.arraycopy(this.lineSeparator, 0, buffer2, context.pos, this.lineSeparator.length);
                    context.pos += this.lineSeparator.length;
                    context.currentLinePos = 0;
                }
            }
        }
    }

    @Override // org.apache.tomcat.util.codec.binary.BaseNCodec
    protected boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    public boolean isUrlSafe() {
        return this.encodeTable == URL_SAFE_ENCODE_TABLE;
    }
}
