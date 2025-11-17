package cn.hutool.bloomfilter.bitMap;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/bitMap/LongMap.class */
public class LongMap implements BitMap, Serializable {
    private static final long serialVersionUID = 1;
    private final long[] longs;

    public LongMap() {
        this.longs = new long[93750000];
    }

    public LongMap(int size) {
        this.longs = new long[size];
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public void add(long i) {
        int r = (int) (i / 64);
        long c = i & 63;
        this.longs[r] = this.longs[r] | (serialVersionUID << ((int) c));
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public boolean contains(long i) {
        int r = (int) (i / 64);
        long c = i & 63;
        return ((this.longs[r] >>> ((int) c)) & serialVersionUID) == serialVersionUID;
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public void remove(long i) {
        int r = (int) (i / 64);
        long c = i & 63;
        long[] jArr = this.longs;
        jArr[r] = jArr[r] & ((serialVersionUID << ((int) c)) ^ (-1));
    }
}
