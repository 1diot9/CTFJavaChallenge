package cn.hutool.core.lang.hash;

import cn.hutool.core.util.ByteUtil;
import java.nio.ByteOrder;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/MetroHash.class */
public class MetroHash {
    private static final long k0_64 = -691005195;
    private static final long k1_64 = -1565916357;
    private static final long k2_64 = 1654206401;
    private static final long k3_64 = 817650473;
    private static final long k0_128 = -935685663;
    private static final long k1_128 = -2042045477;
    private static final long k2_128 = 2078195771;
    private static final long k3_128 = 794325157;

    public static long hash64(byte[] data) {
        return hash64(data, 1337L);
    }

    public static Number128 hash128(byte[] data) {
        return hash128(data, 1337L);
    }

    public static long hash64(byte[] data, long seed) {
        byte[] buffer = data;
        long hash = (seed + k2_64) * k0_64;
        long v0 = hash;
        long v1 = hash;
        long v2 = hash;
        long v3 = hash;
        if (buffer.length >= 32) {
            while (buffer.length >= 32) {
                v0 = rotateLeft64(v0 + (littleEndian64(buffer, 0) * k0_64), -29) + v2;
                v1 = rotateLeft64(v1 + (littleEndian64(buffer, 8) * k1_64), -29) + v3;
                v2 = rotateLeft64(v2 + (littleEndian64(buffer, 24) * k2_64), -29) + v0;
                v3 = rotateLeft64(v3 + (littleEndian64(buffer, 32) * k3_64), -29) + v1;
                buffer = Arrays.copyOfRange(buffer, 32, buffer.length);
            }
            long v22 = v2 ^ (rotateLeft64(((v0 + v3) * k0_64) + v1, -37) * k1_64);
            long v32 = v3 ^ (rotateLeft64(((v1 + v22) * k1_64) + v0, -37) * k0_64);
            hash += (v0 ^ (rotateLeft64(((v0 + v22) * k0_64) + v32, -37) * k1_64)) ^ (v1 ^ (rotateLeft64(((v1 + v32) * k1_64) + v22, -37) * k0_64));
        }
        if (buffer.length >= 16) {
            long v02 = hash + (littleEndian64(buffer, 0) * k2_64);
            long v03 = rotateLeft64(v02, -29) * k3_64;
            long v12 = hash + (littleEndian64(buffer, 8) * k2_64);
            long v13 = rotateLeft64(v12, -29) * k3_64;
            hash += v13 ^ (rotateLeft64(v13 * k3_64, -21) + (v03 ^ (rotateLeft64(v03 * k0_64, -21) + v13)));
            buffer = Arrays.copyOfRange(buffer, 16, buffer.length);
        }
        if (buffer.length >= 8) {
            long hash2 = hash + (littleEndian64(buffer, 0) * k3_64);
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
            hash = hash2 ^ (rotateLeft64(hash2, -55) * k1_64);
        }
        if (buffer.length >= 4) {
            long hash3 = hash + (littleEndian32(Arrays.copyOfRange(buffer, 0, 4)) * k3_64);
            hash = hash3 ^ (rotateLeft64(hash3, -26) * k1_64);
            buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
        }
        if (buffer.length >= 2) {
            long hash4 = hash + (littleEndian16(Arrays.copyOfRange(buffer, 0, 2)) * k3_64);
            buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
            hash = hash4 ^ (rotateLeft64(hash4, -48) * k1_64);
        }
        if (buffer.length >= 1) {
            long hash5 = hash + (buffer[0] * k3_64);
            hash = hash5 ^ (rotateLeft64(hash5, -38) * k1_64);
        }
        long hash6 = (hash ^ rotateLeft64(hash, -28)) * k0_64;
        return hash6 ^ rotateLeft64(hash6, -29);
    }

    public static Number128 hash128(byte[] data, long seed) {
        long v3;
        byte[] buffer = data;
        long v0 = (seed - k0_128) * k3_128;
        long v1 = (seed + k1_128) * k2_128;
        if (buffer.length >= 32) {
            long v2 = (seed + k0_128) * k2_128;
            long j = (seed - k1_128) * k3_128;
            while (true) {
                v3 = j;
                if (buffer.length < 32) {
                    break;
                }
                long v02 = v0 + (littleEndian64(buffer, 0) * k0_128);
                byte[] buffer2 = Arrays.copyOfRange(buffer, 8, buffer.length);
                v0 = rotateRight(v02, 29) + v2;
                long v12 = v1 + (littleEndian64(buffer2, 0) * k1_128);
                byte[] buffer3 = Arrays.copyOfRange(buffer2, 8, buffer2.length);
                v1 = rotateRight(v12, 29) + v3;
                long v22 = v2 + (littleEndian64(buffer3, 0) * k2_128);
                byte[] buffer4 = Arrays.copyOfRange(buffer3, 8, buffer3.length);
                v2 = rotateRight(v22, 29) + v0;
                long v32 = littleEndian64(buffer4, 0) * k3_128;
                buffer = Arrays.copyOfRange(buffer4, 8, buffer4.length);
                j = rotateRight(v32, 29) + v1;
            }
            long v23 = v2 ^ (rotateRight(((v0 + v3) * k0_128) + v1, 21) * k1_128);
            long v33 = v3 ^ (rotateRight(((v1 + v23) * k1_128) + v0, 21) * k0_128);
            v0 ^= rotateRight(((v0 + v23) * k0_128) + v33, 21) * k1_128;
            v1 ^= rotateRight(((v1 + v33) * k1_128) + v23, 21) * k0_128;
        }
        if (buffer.length >= 16) {
            long v03 = v0 + (littleEndian64(buffer, 0) * k2_128);
            byte[] buffer5 = Arrays.copyOfRange(buffer, 8, buffer.length);
            long v04 = rotateRight(v03, 33) * k3_128;
            long v13 = v1 + (littleEndian64(buffer5, 0) * k2_128);
            buffer = Arrays.copyOfRange(buffer5, 8, buffer5.length);
            long v14 = rotateRight(v13, 33) * k3_128;
            v0 = v04 ^ (rotateRight((v04 * k2_128) + v14, 45) + k1_128);
            v1 = v14 ^ (rotateRight((v14 * k3_128) + v0, 45) + k0_128);
        }
        if (buffer.length >= 8) {
            long v05 = v0 + (littleEndian64(buffer, 0) * k2_128);
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
            long v06 = rotateRight(v05, 33) * k3_128;
            v0 = v06 ^ (rotateRight((v06 * k2_128) + v1, 27) * k1_128);
        }
        if (buffer.length >= 4) {
            long v15 = v1 + (littleEndian32(buffer) * k2_128);
            buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
            long v16 = rotateRight(v15, 33) * k3_128;
            v1 = v16 ^ (rotateRight((v16 * k3_128) + v0, 46) * k0_128);
        }
        if (buffer.length >= 2) {
            long v07 = v0 + (littleEndian16(buffer) * k2_128);
            buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
            long v08 = rotateRight(v07, 33) * k3_128;
            v0 = v08 ^ (rotateRight((v08 * k2_128) * v1, 22) * k1_128);
        }
        if (buffer.length >= 1) {
            long v17 = rotateRight(v1 + (buffer[0] * k2_128), 33) * k3_128;
            v1 = v17 ^ (rotateRight((v17 * k3_128) + v0, 58) * k0_128);
        }
        long v09 = v0 + rotateRight((v0 * k0_128) + v1, 13);
        long v18 = v1 + rotateRight((v1 * k1_128) + v09, 37);
        long v010 = v09 + rotateRight((v09 * k2_128) + v18, 13);
        return new Number128(v010, v18 + rotateRight((v18 * k3_128) + v010, 37));
    }

    private static long littleEndian64(byte[] b, int start) {
        return ByteUtil.bytesToLong(b, start, ByteOrder.LITTLE_ENDIAN);
    }

    private static int littleEndian32(byte[] b) {
        return b[0] | (b[1] << 8) | (b[2] << 16) | (b[3] << 24);
    }

    private static int littleEndian16(byte[] b) {
        return ByteUtil.bytesToShort(b, ByteOrder.LITTLE_ENDIAN);
    }

    private static long rotateLeft64(long x, int k) {
        int s = k & (64 - 1);
        return (x << s) | (x >> (64 - s));
    }

    private static long rotateRight(long val, int shift) {
        return (val >> shift) | (val << (64 - shift));
    }
}
