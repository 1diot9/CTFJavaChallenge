package cn.hutool.bloomfilter.bitMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/bitMap/BitMap.class */
public interface BitMap {
    public static final int MACHINE32 = 32;
    public static final int MACHINE64 = 64;

    void add(long j);

    boolean contains(long j);

    void remove(long j);
}
