package cn.hutool.core.lang.hash;

import cn.hutool.core.util.ByteUtil;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/CityHash.class */
public class CityHash {
    private static final long k0 = -4348849565147123417L;
    private static final long k1 = -5435081209227447693L;
    private static final long k2 = -7286425919675154353L;
    private static final long kMul = -7070675565921424023L;
    private static final int c1 = -862048943;
    private static final int c2 = 461845907;

    public static int hash32(byte[] data) {
        int len = data.length;
        if (len <= 24) {
            if (len <= 12) {
                return len <= 4 ? hash32Len0to4(data) : hash32Len5to12(data);
            }
            return hash32Len13to24(data);
        }
        int g = c1 * len;
        int a0 = rotate32(fetch32(data, len - 4) * c1, 17) * c2;
        int a1 = rotate32(fetch32(data, len - 8) * c1, 17) * c2;
        int a2 = rotate32(fetch32(data, len - 16) * c1, 17) * c2;
        int a3 = rotate32(fetch32(data, len - 12) * c1, 17) * c2;
        int a4 = rotate32(fetch32(data, len - 20) * c1, 17) * c2;
        int h = len ^ a0;
        int h2 = (rotate32(((rotate32(h, 19) * 5) - 430675100) ^ a2, 19) * 5) - 430675100;
        int g2 = (rotate32(((rotate32(g ^ a1, 19) * 5) - 430675100) ^ a3, 19) * 5) - 430675100;
        int f = g + a4;
        int f2 = (rotate32(f, 19) * 5) - 430675100;
        int iters = (len - 1) / 20;
        int pos = 0;
        do {
            int a02 = rotate32(fetch32(data, pos) * c1, 17) * c2;
            int a12 = fetch32(data, pos + 4);
            int a22 = rotate32(fetch32(data, pos + 8) * c1, 17) * c2;
            int a32 = rotate32(fetch32(data, pos + 12) * c1, 17) * c2;
            int a42 = fetch32(data, pos + 16);
            int h3 = (rotate32(h2 ^ a02, 18) * 5) - 430675100;
            int f3 = rotate32(f2 + a12, 19) * c1;
            int g3 = (rotate32(g2 + a22, 18) * 5) - 430675100;
            int h4 = (rotate32(h3 ^ (a32 + a12), 19) * 5) - 430675100;
            f2 = Integer.reverseBytes(g3 ^ a42) * 5;
            g2 = Integer.reverseBytes(h4 + (a42 * 5));
            h2 = f3 + a02;
            pos += 20;
            iters--;
        } while (iters != 0);
        int g4 = rotate32(rotate32(g2, 11) * c1, 17) * c1;
        return rotate32((rotate32((rotate32((rotate32(h2 + g4, 19) * 5) - 430675100, 17) * c1) + (rotate32(rotate32(f2, 11) * c1, 17) * c1), 19) * 5) - 430675100, 17) * c1;
    }

    public static long hash64(byte[] data) {
        int len = data.length;
        if (len <= 32) {
            if (len <= 16) {
                return hashLen0to16(data);
            }
            return hashLen17to32(data);
        }
        if (len <= 64) {
            return hashLen33to64(data);
        }
        long x = fetch64(data, len - 40);
        long y = fetch64(data, len - 16) + fetch64(data, len - 56);
        long z = hashLen16(fetch64(data, len - 48) + len, fetch64(data, len - 24));
        Number128 v = weakHashLen32WithSeeds(data, len - 64, len, z);
        Number128 w = weakHashLen32WithSeeds(data, len - 32, y + k1, x);
        long x2 = (x * k1) + fetch64(data, 0);
        int len2 = (len - 1) & (-64);
        int pos = 0;
        do {
            long x3 = rotate64(x2 + y + v.getLowValue() + fetch64(data, pos + 8), 37) * k1;
            long y2 = rotate64(y + v.getHighValue() + fetch64(data, pos + 48), 42) * k1;
            long x4 = x3 ^ w.getHighValue();
            y = y2 + v.getLowValue() + fetch64(data, pos + 40);
            long z2 = rotate64(z + w.getLowValue(), 33) * k1;
            v = weakHashLen32WithSeeds(data, pos, v.getHighValue() * k1, x4 + w.getLowValue());
            w = weakHashLen32WithSeeds(data, pos + 32, z2 + w.getHighValue(), y + fetch64(data, pos + 16));
            x2 = z2;
            z = x4;
            pos += 64;
            len2 -= 64;
        } while (len2 != 0);
        return hashLen16(hashLen16(v.getLowValue(), w.getLowValue()) + (shiftMix(y) * k1) + z, hashLen16(v.getHighValue(), w.getHighValue()) + x2);
    }

    public static long hash64(byte[] data, long seed0, long seed1) {
        return hashLen16(hash64(data) - seed0, seed1);
    }

    public static long hash64(byte[] data, long seed) {
        return hash64(data, k2, seed);
    }

    public static Number128 hash128(byte[] data) {
        int len = data.length;
        if (len >= 16) {
            return hash128(data, 16, new Number128(fetch64(data, 0), fetch64(data, 8) + k0));
        }
        return hash128(data, 0, new Number128(k0, k1));
    }

    public static Number128 hash128(byte[] data, Number128 seed) {
        return hash128(data, 0, seed);
    }

    private static Number128 hash128(byte[] byteArray, int start, Number128 seed) {
        int len = byteArray.length - start;
        if (len < 128) {
            return cityMurmur(Arrays.copyOfRange(byteArray, start, byteArray.length), seed);
        }
        Number128 v = new Number128(0L, 0L);
        Number128 w = new Number128(0L, 0L);
        long x = seed.getLowValue();
        long y = seed.getHighValue();
        long z = len * k1;
        v.setLowValue((rotate64(y ^ k1, 49) * k1) + fetch64(byteArray, start));
        v.setHighValue((rotate64(v.getLowValue(), 42) * k1) + fetch64(byteArray, start + 8));
        w.setLowValue((rotate64(y + z, 35) * k1) + x);
        w.setHighValue(rotate64(x + fetch64(byteArray, start + 88), 53) * k1);
        int pos = start;
        do {
            long x2 = rotate64(x + y + v.getLowValue() + fetch64(byteArray, pos + 8), 37) * k1;
            long y2 = rotate64(y + v.getHighValue() + fetch64(byteArray, pos + 48), 42) * k1;
            long x3 = x2 ^ w.getHighValue();
            long y3 = y2 + v.getLowValue() + fetch64(byteArray, pos + 40);
            long z2 = rotate64(z + w.getLowValue(), 33) * k1;
            Number128 v2 = weakHashLen32WithSeeds(byteArray, pos, v.getHighValue() * k1, x3 + w.getLowValue());
            Number128 w2 = weakHashLen32WithSeeds(byteArray, pos + 32, z2 + w.getHighValue(), y3 + fetch64(byteArray, pos + 16));
            int pos2 = pos + 64;
            long x4 = rotate64(z2 + y3 + v2.getLowValue() + fetch64(byteArray, pos2 + 8), 37) * k1;
            long y4 = rotate64(y3 + v2.getHighValue() + fetch64(byteArray, pos2 + 48), 42) * k1;
            long x5 = x4 ^ w2.getHighValue();
            y = y4 + v2.getLowValue() + fetch64(byteArray, pos2 + 40);
            long z3 = rotate64(x3 + w2.getLowValue(), 33) * k1;
            v = weakHashLen32WithSeeds(byteArray, pos2, v2.getHighValue() * k1, x5 + w2.getLowValue());
            w = weakHashLen32WithSeeds(byteArray, pos2 + 32, z3 + w2.getHighValue(), y + fetch64(byteArray, pos2 + 16));
            x = z3;
            z = x5;
            pos = pos2 + 64;
            len -= 128;
        } while (len >= 128);
        long x6 = x + (rotate64(v.getLowValue() + z, 49) * k0);
        long y5 = (y * k0) + rotate64(w.getHighValue(), 37);
        long z4 = (z * k0) + rotate64(w.getLowValue(), 27);
        w.setLowValue(w.getLowValue() * 9);
        v.setLowValue(v.getLowValue() * k0);
        int tail_done = 0;
        while (tail_done < len) {
            tail_done += 32;
            y5 = (rotate64(x6 + y5, 42) * k0) + v.getHighValue();
            w.setLowValue(w.getLowValue() + fetch64(byteArray, ((pos + len) - tail_done) + 16));
            x6 = (x6 * k0) + w.getLowValue();
            z4 += w.getHighValue() + fetch64(byteArray, (pos + len) - tail_done);
            w.setHighValue(w.getHighValue() + v.getLowValue());
            v = weakHashLen32WithSeeds(byteArray, (pos + len) - tail_done, v.getLowValue() + z4, v.getHighValue());
            v.setLowValue(v.getLowValue() * k0);
        }
        long x7 = hashLen16(x6, v.getLowValue());
        long y6 = hashLen16(y5 + z4, w.getLowValue());
        return new Number128(hashLen16(x7 + v.getHighValue(), w.getHighValue()) + y6, hashLen16(x7 + w.getHighValue(), y6 + v.getHighValue()));
    }

    private static int hash32Len0to4(byte[] byteArray) {
        int b = 0;
        int c = 9;
        int len = byteArray.length;
        for (byte b2 : byteArray) {
            b = (b * c1) + b2;
            c ^= b;
        }
        return fmix(mur(b, mur(len, c)));
    }

    private static int hash32Len5to12(byte[] byteArray) {
        int len = byteArray.length;
        int b = len * 5;
        int a = len + fetch32(byteArray, 0);
        int b2 = b + fetch32(byteArray, len - 4);
        int c = 9 + fetch32(byteArray, (len >>> 1) & 4);
        return fmix(mur(c, mur(b2, mur(a, b))));
    }

    private static int hash32Len13to24(byte[] byteArray) {
        int len = byteArray.length;
        int a = fetch32(byteArray, (len >>> 1) - 4);
        int b = fetch32(byteArray, 4);
        int c = fetch32(byteArray, len - 8);
        int d = fetch32(byteArray, len >>> 1);
        int e = fetch32(byteArray, 0);
        int f = fetch32(byteArray, len - 4);
        return fmix(mur(f, mur(e, mur(d, mur(c, mur(b, mur(a, len)))))));
    }

    private static long hashLen0to16(byte[] byteArray) {
        int len = byteArray.length;
        if (len >= 8) {
            long mul = k2 + (len * 2);
            long a = fetch64(byteArray, 0) + k2;
            long b = fetch64(byteArray, len - 8);
            long c = (rotate64(b, 37) * mul) + a;
            long d = (rotate64(a, 25) + b) * mul;
            return hashLen16(c, d, mul);
        }
        if (len >= 4) {
            long mul2 = k2 + (len * 2);
            long a2 = fetch32(byteArray, 0) & 4294967295L;
            return hashLen16(len + (a2 << 3), fetch32(byteArray, len - 4) & 4294967295L, mul2);
        }
        if (len > 0) {
            int a3 = byteArray[0] & 255;
            int b2 = byteArray[len >>> 1] & 255;
            int c3 = byteArray[len - 1] & 255;
            int y = a3 + (b2 << 8);
            int z = len + (c3 << 2);
            return shiftMix((y * k2) ^ (z * k0)) * k2;
        }
        return k2;
    }

    private static long hashLen17to32(byte[] byteArray) {
        int len = byteArray.length;
        long mul = k2 + (len * 2);
        long a = fetch64(byteArray, 0) * k1;
        long b = fetch64(byteArray, 8);
        long c = fetch64(byteArray, len - 8) * mul;
        long d = fetch64(byteArray, len - 16) * k2;
        return hashLen16(rotate64(a + b, 43) + rotate64(c, 30) + d, a + rotate64(b + k2, 18) + c, mul);
    }

    private static long hashLen33to64(byte[] byteArray) {
        int len = byteArray.length;
        long mul = k2 + (len * 2);
        long a = fetch64(byteArray, 0) * k2;
        long b = fetch64(byteArray, 8);
        long c = fetch64(byteArray, len - 24);
        long d = fetch64(byteArray, len - 32);
        long e = fetch64(byteArray, 16) * k2;
        long f = fetch64(byteArray, 24) * 9;
        long g = fetch64(byteArray, len - 8);
        long h = fetch64(byteArray, len - 16) * mul;
        long u = rotate64(a + g, 43) + ((rotate64(b, 30) + c) * 9);
        long v = ((a + g) ^ d) + f + 1;
        long w = Long.reverseBytes((u + v) * mul) + h;
        long x = rotate64(e + f, 42) + c;
        long y = (Long.reverseBytes((v + w) * mul) + g) * mul;
        long z = e + f + c;
        return (shiftMix(((z + Long.reverseBytes(((x + z) * mul) + y) + b) * mul) + d + h) * mul) + x;
    }

    private static long fetch64(byte[] byteArray, int start) {
        return ByteUtil.bytesToLong(byteArray, start, ByteUtil.CPU_ENDIAN);
    }

    private static int fetch32(byte[] byteArray, int start) {
        return ByteUtil.bytesToInt(byteArray, start, ByteUtil.CPU_ENDIAN);
    }

    private static long rotate64(long val, int shift) {
        return shift == 0 ? val : (val >>> shift) | (val << (64 - shift));
    }

    private static int rotate32(int val, int shift) {
        return shift == 0 ? val : (val >>> shift) | (val << (32 - shift));
    }

    private static long hashLen16(long u, long v, long mul) {
        long a = (u ^ v) * mul;
        long b = (v ^ (a ^ (a >>> 47))) * mul;
        return (b ^ (b >>> 47)) * mul;
    }

    private static long hashLen16(long u, long v) {
        return hash128to64(new Number128(u, v));
    }

    private static long hash128to64(Number128 number128) {
        long a = (number128.getLowValue() ^ number128.getHighValue()) * kMul;
        long b = (number128.getHighValue() ^ (a ^ (a >>> 47))) * kMul;
        return (b ^ (b >>> 47)) * kMul;
    }

    private static long shiftMix(long val) {
        return val ^ (val >>> 47);
    }

    private static int fmix(int h) {
        int h2 = (h ^ (h >>> 16)) * (-2048144789);
        int h3 = (h2 ^ (h2 >>> 13)) * (-1028477387);
        return h3 ^ (h3 >>> 16);
    }

    private static int mur(int a, int h) {
        return (rotate32(h ^ (rotate32(a * c1, 17) * c2), 19) * 5) - 430675100;
    }

    private static Number128 weakHashLen32WithSeeds(long w, long x, long y, long z, long a, long b) {
        long a2 = a + w;
        long b2 = rotate64(b + a2 + z, 21);
        long a3 = a2 + x + y;
        return new Number128(a3 + z, b2 + rotate64(a3, 44) + a2);
    }

    private static Number128 weakHashLen32WithSeeds(byte[] byteArray, int start, long a, long b) {
        return weakHashLen32WithSeeds(fetch64(byteArray, start), fetch64(byteArray, start + 8), fetch64(byteArray, start + 16), fetch64(byteArray, start + 24), a, b);
    }

    private static Number128 cityMurmur(byte[] byteArray, Number128 seed) {
        long c;
        long d;
        long a;
        int len = byteArray.length;
        long a2 = seed.getLowValue();
        long b = seed.getHighValue();
        int l = len - 16;
        if (l <= 0) {
            a = shiftMix(a2 * k1) * k1;
            c = (b * k1) + hashLen0to16(byteArray);
            d = shiftMix(a + (len >= 8 ? fetch64(byteArray, 0) : c));
        } else {
            c = hashLen16(fetch64(byteArray, len - 8) + k1, a2);
            d = hashLen16(b + len, c + fetch64(byteArray, len - 16));
            a = a2 + d;
            int pos = 0;
            do {
                a = (a ^ (shiftMix(fetch64(byteArray, pos) * k1) * k1)) * k1;
                b ^= a;
                c = (c ^ (shiftMix(fetch64(byteArray, pos + 8) * k1) * k1)) * k1;
                d ^= c;
                pos += 16;
                l -= 16;
            } while (l > 0);
        }
        long a3 = hashLen16(a, c);
        long b2 = hashLen16(d, b);
        return new Number128(a3 ^ b2, hashLen16(b2, a3));
    }
}
