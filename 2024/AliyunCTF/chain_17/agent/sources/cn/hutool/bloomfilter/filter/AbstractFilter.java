package cn.hutool.bloomfilter.filter;

import cn.hutool.bloomfilter.BloomFilter;
import cn.hutool.bloomfilter.bitMap.BitMap;
import cn.hutool.bloomfilter.bitMap.IntMap;
import cn.hutool.bloomfilter.bitMap.LongMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/AbstractFilter.class */
public abstract class AbstractFilter implements BloomFilter {
    private static final long serialVersionUID = 1;
    protected static int DEFAULT_MACHINE_NUM = 32;
    private BitMap bm;
    protected long size;

    public abstract long hash(String str);

    public AbstractFilter(long maxValue, int machineNum) {
        this.bm = null;
        init(maxValue, machineNum);
    }

    public AbstractFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public void init(long maxValue, int machineNum) {
        this.size = maxValue;
        switch (machineNum) {
            case 32:
                this.bm = new IntMap((int) (this.size / machineNum));
                return;
            case 64:
                this.bm = new LongMap((int) (this.size / machineNum));
                return;
            default:
                throw new RuntimeException("Error Machine number!");
        }
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean contains(String str) {
        return this.bm.contains(Math.abs(hash(str)));
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean add(String str) {
        long hash = Math.abs(hash(str));
        if (this.bm.contains(hash)) {
            return false;
        }
        this.bm.add(hash);
        return true;
    }
}
