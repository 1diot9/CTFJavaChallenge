package cn.hutool.bloomfilter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/BloomFilterUtil.class */
public class BloomFilterUtil {
    public static BitSetBloomFilter createBitSet(int c, int n, int k) {
        return new BitSetBloomFilter(c, n, k);
    }

    public static BitMapBloomFilter createBitMap(int m) {
        return new BitMapBloomFilter(m);
    }
}
