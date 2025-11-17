package org.h2.compress;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/CompressLZF.class */
public final class CompressLZF implements Compressor {
    private static final int HASH_SIZE = 16384;
    private static final int MAX_LITERAL = 32;
    private static final int MAX_OFF = 8192;
    private static final int MAX_REF = 264;
    private int[] cachedHashTable;

    @Override // org.h2.compress.Compressor
    public void setOptions(String str) {
    }

    private static int first(byte[] bArr, int i) {
        return (bArr[i] << 8) | (bArr[i + 1] & 255);
    }

    private static int first(ByteBuffer byteBuffer, int i) {
        return (byteBuffer.get(i) << 8) | (byteBuffer.get(i + 1) & 255);
    }

    private static int next(int i, byte[] bArr, int i2) {
        return (i << 8) | (bArr[i2 + 2] & 255);
    }

    private static int next(int i, ByteBuffer byteBuffer, int i2) {
        return (i << 8) | (byteBuffer.get(i2 + 2) & 255);
    }

    private static int hash(int i) {
        return ((i * 2777) >> 9) & 16383;
    }

    @Override // org.h2.compress.Compressor
    public int compress(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        int i4;
        int i5;
        int i6 = i2 + i;
        if (this.cachedHashTable == null) {
            this.cachedHashTable = new int[16384];
        }
        int[] iArr = this.cachedHashTable;
        int i7 = 0;
        int i8 = i3 + 1;
        int first = first(bArr, i);
        while (i < i6 - 4) {
            byte b = bArr[i + 2];
            first = (first << 8) + (b & 255);
            int hash = hash(first);
            int i9 = iArr[hash];
            iArr[hash] = i;
            if (i9 < i && i9 > i && (i4 = (i - i9) - 1) < 8192 && bArr[i9 + 2] == b && bArr[i9 + 1] == ((byte) (first >> 8)) && bArr[i9] == ((byte) (first >> 16))) {
                int i10 = (i6 - i) - 2;
                if (i10 > 264) {
                    i10 = 264;
                }
                if (i7 == 0) {
                    i8--;
                } else {
                    bArr2[(i8 - i7) - 1] = (byte) (i7 - 1);
                    i7 = 0;
                }
                int i11 = 3;
                while (i11 < i10 && bArr[i9 + i11] == bArr[i + i11]) {
                    i11++;
                }
                int i12 = i11 - 2;
                if (i12 < 7) {
                    int i13 = i8;
                    i5 = i8 + 1;
                    bArr2[i13] = (byte) ((i4 >> 8) + (i12 << 5));
                } else {
                    int i14 = i8;
                    int i15 = i8 + 1;
                    bArr2[i14] = (byte) ((i4 >> 8) + 224);
                    i5 = i15 + 1;
                    bArr2[i15] = (byte) (i12 - 7);
                }
                bArr2[i5] = (byte) i4;
                i8 = i5 + 1 + 1;
                int i16 = i + i12;
                int next = next(first(bArr, i16), bArr, i16);
                int i17 = i16 + 1;
                iArr[hash(next)] = i16;
                first = next(next, bArr, i17);
                i = i17 + 1;
                iArr[hash(first)] = i17;
            } else {
                int i18 = i8;
                i8++;
                int i19 = i;
                i++;
                bArr2[i18] = bArr[i19];
                i7++;
                if (i7 == 32) {
                    bArr2[(i8 - i7) - 1] = (byte) (i7 - 1);
                    i7 = 0;
                    i8++;
                }
            }
        }
        while (i < i6) {
            int i20 = i8;
            i8++;
            int i21 = i;
            i++;
            bArr2[i20] = bArr[i21];
            i7++;
            if (i7 == 32) {
                bArr2[(i8 - i7) - 1] = (byte) (i7 - 1);
                i7 = 0;
                i8++;
            }
        }
        bArr2[(i8 - i7) - 1] = (byte) (i7 - 1);
        if (i7 == 0) {
            i8--;
        }
        return i8;
    }

    public int compress(ByteBuffer byteBuffer, int i, byte[] bArr, int i2) {
        int i3;
        int i4;
        int capacity = byteBuffer.capacity();
        if (this.cachedHashTable == null) {
            this.cachedHashTable = new int[16384];
        }
        int[] iArr = this.cachedHashTable;
        int i5 = 0;
        int i6 = i2 + 1;
        int first = first(byteBuffer, i);
        while (i < capacity - 4) {
            byte b = byteBuffer.get(i + 2);
            first = (first << 8) + (b & 255);
            int hash = hash(first);
            int i7 = iArr[hash];
            iArr[hash] = i;
            if (i7 < i && i7 > i && (i3 = (i - i7) - 1) < 8192 && byteBuffer.get(i7 + 2) == b && byteBuffer.get(i7 + 1) == ((byte) (first >> 8)) && byteBuffer.get(i7) == ((byte) (first >> 16))) {
                int i8 = (capacity - i) - 2;
                if (i8 > 264) {
                    i8 = 264;
                }
                if (i5 == 0) {
                    i6--;
                } else {
                    bArr[(i6 - i5) - 1] = (byte) (i5 - 1);
                    i5 = 0;
                }
                int i9 = 3;
                while (i9 < i8 && byteBuffer.get(i7 + i9) == byteBuffer.get(i + i9)) {
                    i9++;
                }
                int i10 = i9 - 2;
                if (i10 < 7) {
                    int i11 = i6;
                    i4 = i6 + 1;
                    bArr[i11] = (byte) ((i3 >> 8) + (i10 << 5));
                } else {
                    int i12 = i6;
                    int i13 = i6 + 1;
                    bArr[i12] = (byte) ((i3 >> 8) + 224);
                    i4 = i13 + 1;
                    bArr[i13] = (byte) (i10 - 7);
                }
                bArr[i4] = (byte) i3;
                i6 = i4 + 1 + 1;
                int i14 = i + i10;
                int next = next(first(byteBuffer, i14), byteBuffer, i14);
                int i15 = i14 + 1;
                iArr[hash(next)] = i14;
                first = next(next, byteBuffer, i15);
                i = i15 + 1;
                iArr[hash(first)] = i15;
            } else {
                int i16 = i6;
                i6++;
                int i17 = i;
                i++;
                bArr[i16] = byteBuffer.get(i17);
                i5++;
                if (i5 == 32) {
                    bArr[(i6 - i5) - 1] = (byte) (i5 - 1);
                    i5 = 0;
                    i6++;
                }
            }
        }
        while (i < capacity) {
            int i18 = i6;
            i6++;
            int i19 = i;
            i++;
            bArr[i18] = byteBuffer.get(i19);
            i5++;
            if (i5 == 32) {
                bArr[(i6 - i5) - 1] = (byte) (i5 - 1);
                i5 = 0;
                i6++;
            }
        }
        bArr[(i6 - i5) - 1] = (byte) (i5 - 1);
        if (i5 == 0) {
            i6--;
        }
        return i6;
    }

    @Override // org.h2.compress.Compressor
    public void expand(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) {
        if (i < 0 || i3 < 0 || i4 < 0) {
            throw new IllegalArgumentException();
        }
        do {
            int i5 = i;
            int i6 = i + 1;
            int i7 = bArr[i5] & Const.MAX_ARRAY_DIMENSIONS;
            if (i7 < 32) {
                int i8 = i7 + 1;
                System.arraycopy(bArr, i6, bArr2, i3, i8);
                i3 += i8;
                i = i6 + i8;
            } else {
                int i9 = i7 >> 5;
                if (i9 == 7) {
                    i6++;
                    i9 += bArr[i6] & Const.MAX_ARRAY_DIMENSIONS;
                }
                int i10 = i9 + 2;
                int i11 = i6;
                i = i6 + 1;
                int i12 = (((-((i7 & 31) << 8)) - 1) - (bArr[i11] & Const.MAX_ARRAY_DIMENSIONS)) + i3;
                if (i3 + i10 >= bArr2.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                for (int i13 = 0; i13 < i10; i13++) {
                    int i14 = i3;
                    i3++;
                    int i15 = i12;
                    i12++;
                    bArr2[i14] = bArr2[i15];
                }
            }
        } while (i3 < i4);
    }

    public static void expand(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        do {
            int i = byteBuffer.get() & 255;
            if (i < 32) {
                int i2 = i + 1;
                for (int i3 = 0; i3 < i2; i3++) {
                    byteBuffer2.put(byteBuffer.get());
                }
            } else {
                int i4 = i >> 5;
                if (i4 == 7) {
                    i4 += byteBuffer.get() & 255;
                }
                int i5 = i4 + 2;
                int position = (((-((i & 31) << 8)) - 1) - (byteBuffer.get() & 255)) + byteBuffer2.position();
                for (int i6 = 0; i6 < i5; i6++) {
                    int i7 = position;
                    position++;
                    byteBuffer2.put(byteBuffer2.get(i7));
                }
            }
        } while (byteBuffer2.position() < byteBuffer2.capacity());
    }

    @Override // org.h2.compress.Compressor
    public int getAlgorithm() {
        return 1;
    }
}
