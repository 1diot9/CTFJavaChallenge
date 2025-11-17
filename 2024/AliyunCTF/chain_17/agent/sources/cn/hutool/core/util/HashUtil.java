package cn.hutool.core.util;

import cn.hutool.core.lang.hash.CityHash;
import cn.hutool.core.lang.hash.MetroHash;
import cn.hutool.core.lang.hash.MurmurHash;
import cn.hutool.core.lang.hash.Number128;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/HashUtil.class */
public class HashUtil {
    public static int additiveHash(String key, int prime) {
        int hash = key.length();
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i);
        }
        return hash % prime;
    }

    public static int rotatingHash(String key, int prime) {
        int hash = key.length();
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 4) ^ (hash >> 28)) ^ key.charAt(i);
        }
        return hash % prime;
    }

    public static int oneByOneHash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            int hash2 = hash + key.charAt(i);
            int hash3 = hash2 + (hash2 << 10);
            hash = hash3 ^ (hash3 >> 6);
        }
        int hash4 = hash + (hash << 3);
        int hash5 = hash4 ^ (hash4 >> 11);
        return hash5 + (hash5 << 15);
    }

    public static int bernstein(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (33 * hash) + key.charAt(i);
        }
        return hash;
    }

    public static int universal(char[] key, int mask, int[] tab) {
        int hash = key.length;
        int len = key.length;
        for (int i = 0; i < (len << 3); i += 8) {
            char k = key[i >> 3];
            if ((k & 1) == 0) {
                hash ^= tab[i];
            }
            if ((k & 2) == 0) {
                hash ^= tab[i + 1];
            }
            if ((k & 4) == 0) {
                hash ^= tab[i + 2];
            }
            if ((k & '\b') == 0) {
                hash ^= tab[i + 3];
            }
            if ((k & 16) == 0) {
                hash ^= tab[i + 4];
            }
            if ((k & ' ') == 0) {
                hash ^= tab[i + 5];
            }
            if ((k & '@') == 0) {
                hash ^= tab[i + 6];
            }
            if ((k & 128) == 0) {
                hash ^= tab[i + 7];
            }
        }
        return hash & mask;
    }

    public static int zobrist(char[] key, int mask, int[][] tab) {
        int hash = key.length;
        for (int i = 0; i < key.length; i++) {
            hash ^= tab[i][key[i]];
        }
        return hash & mask;
    }

    public static int fnvHash(byte[] data) {
        int hash = -2128831035;
        for (byte b : data) {
            hash = (hash ^ b) * 16777619;
        }
        int hash2 = hash + (hash << 13);
        int hash3 = hash2 ^ (hash2 >> 7);
        int hash4 = hash3 + (hash3 << 3);
        int hash5 = hash4 ^ (hash4 >> 17);
        return Math.abs(hash5 + (hash5 << 5));
    }

    public static int fnvHash(String data) {
        int hash = -2128831035;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash ^ data.charAt(i)) * 16777619;
        }
        int hash2 = hash + (hash << 13);
        int hash3 = hash2 ^ (hash2 >> 7);
        int hash4 = hash3 + (hash3 << 3);
        int hash5 = hash4 ^ (hash4 >> 17);
        return Math.abs(hash5 + (hash5 << 5));
    }

    public static int intHash(int key) {
        int key2 = key + ((key << 15) ^ (-1));
        int key3 = key2 ^ (key2 >>> 10);
        int key4 = key3 + (key3 << 3);
        int key5 = key4 ^ (key4 >>> 6);
        int key6 = key5 + ((key5 << 11) ^ (-1));
        return key6 ^ (key6 >>> 16);
    }

    public static int rsHash(String str) {
        int a = 63689;
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash * a) + str.charAt(i);
            a *= 378551;
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int jsHash(String str) {
        int hash = 1315423911;
        for (int i = 0; i < str.length(); i++) {
            hash ^= ((hash << 5) + str.charAt(i)) + (hash >> 2);
        }
        return Math.abs(hash) & Integer.MAX_VALUE;
    }

    public static int pjwHash(String str) {
        int threeQuarters = (32 * 3) / 4;
        int oneEighth = 32 / 8;
        int highBits = (-1) << (32 - oneEighth);
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash << oneEighth) + str.charAt(i);
            int test = hash & highBits;
            if (test != 0) {
                hash = (hash ^ (test >> threeQuarters)) & (highBits ^ (-1));
            }
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int elfHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 4) + str.charAt(i);
            int x = (int) (hash & 4026531840L);
            if (x != 0) {
                hash = (hash ^ (x >> 24)) & (x ^ (-1));
            }
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int bkdrHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash * Opcodes.LXOR) + str.charAt(i);
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int sdbmHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = ((str.charAt(i) + (hash << 6)) + (hash << 16)) - hash;
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int djbHash(String str) {
        int hash = 5381;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 5) + hash + str.charAt(i);
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int dekHash(String str) {
        int hash = str.length();
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
        }
        return hash & Integer.MAX_VALUE;
    }

    public static int apHash(String str) {
        int charAt;
        int i;
        int hash = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            int i3 = hash;
            if ((i2 & 1) == 0) {
                charAt = (hash << 7) ^ str.charAt(i2);
                i = hash >> 3;
            } else {
                charAt = ((hash << 11) ^ str.charAt(i2)) ^ (hash >> 5);
                i = -1;
            }
            hash = i3 ^ (charAt ^ i);
        }
        return hash;
    }

    public static long tianlHash(String str) {
        long hash;
        int iLength = str.length();
        if (iLength == 0) {
            return 0L;
        }
        if (iLength <= 256) {
            hash = 16777216 * (iLength - 1);
        } else {
            hash = 4278190080L;
        }
        if (iLength <= 96) {
            for (int i = 1; i <= iLength; i++) {
                char ucChar = str.charAt(i - 1);
                if (ucChar <= 'Z' && ucChar >= 'A') {
                    ucChar = (char) (ucChar + ' ');
                }
                hash += ((((((3 * i) * ucChar) * ucChar) + ((5 * i) * ucChar)) + (7 * i)) + (11 * ucChar)) % 16777216;
            }
        } else {
            for (int i2 = 1; i2 <= 96; i2++) {
                char ucChar2 = str.charAt(((i2 + iLength) - 96) - 1);
                if (ucChar2 <= 'Z' && ucChar2 >= 'A') {
                    ucChar2 = (char) (ucChar2 + ' ');
                }
                hash += ((((((3 * i2) * ucChar2) * ucChar2) + ((5 * i2) * ucChar2)) + (7 * i2)) + (11 * ucChar2)) % 16777216;
            }
        }
        if (hash < 0) {
            hash *= -1;
        }
        return hash;
    }

    public static int javaDefaultHash(String str) {
        int h = 0;
        int off = 0;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            h = (31 * h) + str.charAt(i2);
        }
        return h;
    }

    public static long mixHash(String str) {
        long hash = str.hashCode();
        return (hash << 32) | fnvHash(str);
    }

    public static int identityHashCode(Object obj) {
        return System.identityHashCode(obj);
    }

    public static int murmur32(byte[] data) {
        return MurmurHash.hash32(data);
    }

    public static long murmur64(byte[] data) {
        return MurmurHash.hash64(data);
    }

    public static long[] murmur128(byte[] data) {
        return MurmurHash.hash128(data);
    }

    public static int cityHash32(byte[] data) {
        return CityHash.hash32(data);
    }

    public static long cityHash64(byte[] data, long seed) {
        return CityHash.hash64(data, seed);
    }

    public static long cityHash64(byte[] data, long seed0, long seed1) {
        return CityHash.hash64(data, seed0, seed1);
    }

    public static long cityHash64(byte[] data) {
        return CityHash.hash64(data);
    }

    public static long[] cityHash128(byte[] data) {
        return CityHash.hash128(data).getLongArray();
    }

    public static long[] cityHash128(byte[] data, Number128 seed) {
        return CityHash.hash128(data, seed).getLongArray();
    }

    public static long metroHash64(byte[] data, long seed) {
        return MetroHash.hash64(data, seed);
    }

    public static long metroHash64(byte[] data) {
        return MetroHash.hash64(data);
    }

    public static long[] metroHash128(byte[] data, long seed) {
        return MetroHash.hash128(data, seed).getLongArray();
    }

    public static long[] metroHash128(byte[] data) {
        return MetroHash.hash128(data).getLongArray();
    }

    public static long hfHash(String data) {
        int length = data.length();
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash += data.charAt(i) * 3 * i;
        }
        if (hash < 0) {
            hash = -hash;
        }
        return hash;
    }

    public static long hfIpHash(String data) {
        int length = data.length();
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash += data.charAt(i % 4) ^ data.charAt(i);
        }
        return hash;
    }
}
