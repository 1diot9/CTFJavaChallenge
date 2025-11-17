package cn.hutool.bloomfilter.bitMap;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/bitMap/IntMap.class */
public class IntMap implements BitMap, Serializable {
    private static final long serialVersionUID = 1;
    private final int[] ints;

    public IntMap() {
        this.ints = new int[93750000];
    }

    public IntMap(int size) {
        this.ints = new int[size];
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public void add(long i) {
        int r = (int) (i / 32);
        int c = (int) (i & 31);
        this.ints[r] = this.ints[r] | (1 << c);
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public boolean contains(long i) {
        int r = (int) (i / 32);
        int c = (int) (i & 31);
        return ((this.ints[r] >>> c) & 1) == 1;
    }

    @Override // cn.hutool.bloomfilter.bitMap.BitMap
    public void remove(long i) {
        int r = (int) (i / 32);
        int c = (int) (i & 31);
        int[] iArr = this.ints;
        iArr[r] = iArr[r] & ((1 << c) ^ (-1));
    }
}
