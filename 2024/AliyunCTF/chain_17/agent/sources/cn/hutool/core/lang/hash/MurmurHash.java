package cn.hutool.core.lang.hash;

import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/MurmurHash.class */
public class MurmurHash implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int C1_32 = -862048943;
    private static final int C2_32 = 461845907;
    private static final int R1_32 = 15;
    private static final int R2_32 = 13;
    private static final int M_32 = 5;
    private static final int N_32 = -430675100;
    private static final long C1 = -8663945395140668459L;
    private static final long C2 = 5545529020109919103L;
    private static final int R1 = 31;
    private static final int R2 = 27;
    private static final int R3 = 33;
    private static final int M = 5;
    private static final int N1 = 1390208809;
    private static final int N2 = 944331445;
    private static final int DEFAULT_SEED = 0;
    private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    private static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;

    public static int hash32(CharSequence data) {
        return hash32(StrUtil.bytes(data, DEFAULT_CHARSET));
    }

    public static int hash32(byte[] data) {
        return hash32(data, data.length, 0);
    }

    public static int hash32(byte[] data, int length, int seed) {
        return hash32(data, 0, length, seed);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int hash32(byte[] data, int offset, int length, int seed) {
        int hash = seed;
        int nblocks = length >> 2;
        for (int i = 0; i < nblocks; i++) {
            int i4 = offset + (i << 2);
            int k = ByteUtil.bytesToInt(data, i4, DEFAULT_ORDER);
            hash = mix32(k, hash);
        }
        int idx = offset + (nblocks << 2);
        int k1 = 0;
        switch ((offset + length) - idx) {
            case 1:
                hash ^= Integer.rotateLeft((k1 ^ (data[idx] & 255)) * C1_32, 15) * C2_32;
                break;
            case 2:
                k1 ^= (data[idx + 1] & 255) << 8;
                hash ^= Integer.rotateLeft((k1 ^ (data[idx] & 255)) * C1_32, 15) * C2_32;
                break;
            case 3:
                k1 = 0 ^ ((data[idx + 2] & 255) << 16);
                k1 ^= (data[idx + 1] & 255) << 8;
                hash ^= Integer.rotateLeft((k1 ^ (data[idx] & 255)) * C1_32, 15) * C2_32;
                break;
        }
        return fmix32(hash ^ length);
    }

    public static long hash64(CharSequence data) {
        return hash64(StrUtil.bytes(data, DEFAULT_CHARSET));
    }

    public static long hash64(byte[] data) {
        return hash64(data, data.length, 0);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static long hash64(byte[] data, int length, int seed) {
        long hash = seed;
        int nblocks = length >> 3;
        for (int i = 0; i < nblocks; i++) {
            int i8 = i << 3;
            long k = ByteUtil.bytesToLong(data, i8, DEFAULT_ORDER);
            hash = (Long.rotateLeft(hash ^ (Long.rotateLeft(k * C1, 31) * C2), 27) * 5) + 1390208809;
        }
        long k1 = 0;
        int tailStart = nblocks << 3;
        switch (length - tailStart) {
            case 1:
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 2:
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 3:
                k1 ^= (data[tailStart + 2] & 255) << 16;
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 4:
                k1 ^= (data[tailStart + 3] & 255) << 24;
                k1 ^= (data[tailStart + 2] & 255) << 16;
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 5:
                k1 ^= (data[tailStart + 4] & 255) << 32;
                k1 ^= (data[tailStart + 3] & 255) << 24;
                k1 ^= (data[tailStart + 2] & 255) << 16;
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 6:
                k1 ^= (data[tailStart + 5] & 255) << 40;
                k1 ^= (data[tailStart + 4] & 255) << 32;
                k1 ^= (data[tailStart + 3] & 255) << 24;
                k1 ^= (data[tailStart + 2] & 255) << 16;
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 7:
                k1 = 0 ^ ((data[tailStart + 6] & 255) << 48);
                k1 ^= (data[tailStart + 5] & 255) << 40;
                k1 ^= (data[tailStart + 4] & 255) << 32;
                k1 ^= (data[tailStart + 3] & 255) << 24;
                k1 ^= (data[tailStart + 2] & 255) << 16;
                k1 ^= (data[tailStart + 1] & 255) << 8;
                hash ^= Long.rotateLeft((k1 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
        }
        return fmix64(hash ^ length);
    }

    public static long[] hash128(CharSequence data) {
        return hash128(StrUtil.bytes(data, DEFAULT_CHARSET));
    }

    public static long[] hash128(byte[] data) {
        return hash128(data, data.length, 0);
    }

    public static long[] hash128(byte[] data, int length, int seed) {
        return hash128(data, 0, length, seed);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static long[] hash128(byte[] data, int offset, int length, int seed) {
        int seed2 = (int) (seed & 4294967295L);
        long h1 = seed2;
        long h2 = seed2;
        int nblocks = length >> 4;
        for (int i = 0; i < nblocks; i++) {
            int i16 = offset + (i << 4);
            long k1 = ByteUtil.bytesToLong(data, i16, DEFAULT_ORDER);
            long k2 = ByteUtil.bytesToLong(data, i16 + 8, DEFAULT_ORDER);
            h1 = ((Long.rotateLeft(h1 ^ (Long.rotateLeft(k1 * C1, 31) * C2), 27) + h2) * 5) + 1390208809;
            h2 = ((Long.rotateLeft(h2 ^ (Long.rotateLeft(k2 * C2, 33) * C1), 31) + h1) * 5) + 944331445;
        }
        long k12 = 0;
        long k22 = 0;
        int tailStart = offset + (nblocks << 4);
        switch ((offset + length) - tailStart) {
            case 1:
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 2:
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 3:
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 4:
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 5:
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 6:
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 7:
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 8:
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 9:
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 10:
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 11:
                k22 ^= (data[tailStart + 10] & 255) << 16;
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 12:
                k22 ^= (data[tailStart + 11] & 255) << 24;
                k22 ^= (data[tailStart + 10] & 255) << 16;
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 13:
                k22 ^= (data[tailStart + 12] & 255) << 32;
                k22 ^= (data[tailStart + 11] & 255) << 24;
                k22 ^= (data[tailStart + 10] & 255) << 16;
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 14:
                k22 ^= (data[tailStart + 13] & 255) << 40;
                k22 ^= (data[tailStart + 12] & 255) << 32;
                k22 ^= (data[tailStart + 11] & 255) << 24;
                k22 ^= (data[tailStart + 10] & 255) << 16;
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
            case 15:
                k22 = 0 ^ ((data[tailStart + 14] & 255) << 48);
                k22 ^= (data[tailStart + 13] & 255) << 40;
                k22 ^= (data[tailStart + 12] & 255) << 32;
                k22 ^= (data[tailStart + 11] & 255) << 24;
                k22 ^= (data[tailStart + 10] & 255) << 16;
                k22 ^= (data[tailStart + 9] & 255) << 8;
                h2 ^= Long.rotateLeft((k22 ^ (data[tailStart + 8] & 255)) * C2, 33) * C1;
                k12 = 0 ^ ((data[tailStart + 7] & 255) << 56);
                k12 ^= (data[tailStart + 6] & 255) << 48;
                k12 ^= (data[tailStart + 5] & 255) << 40;
                k12 ^= (data[tailStart + 4] & 255) << 32;
                k12 ^= (data[tailStart + 3] & 255) << 24;
                k12 ^= (data[tailStart + 2] & 255) << 16;
                k12 ^= (data[tailStart + 1] & 255) << 8;
                h1 ^= Long.rotateLeft((k12 ^ (data[tailStart] & 255)) * C1, 31) * C2;
                break;
        }
        long h22 = h2 ^ length;
        long h12 = (h1 ^ length) + h22;
        long h23 = h22 + h12;
        long h13 = fmix64(h12);
        long h24 = fmix64(h23);
        long h14 = h13 + h24;
        return new long[]{h14, h24 + h14};
    }

    private static int mix32(int k, int hash) {
        return (Integer.rotateLeft(hash ^ (Integer.rotateLeft(k * C1_32, 15) * C2_32), 13) * 5) + N_32;
    }

    private static int fmix32(int hash) {
        int hash2 = (hash ^ (hash >>> 16)) * (-2048144789);
        int hash3 = (hash2 ^ (hash2 >>> 13)) * (-1028477387);
        return hash3 ^ (hash3 >>> 16);
    }

    private static long fmix64(long h) {
        long h2 = (h ^ (h >>> 33)) * (-49064778989728563L);
        long h3 = (h2 ^ (h2 >>> 33)) * (-4265267296055464877L);
        return h3 ^ (h3 >>> 33);
    }
}
