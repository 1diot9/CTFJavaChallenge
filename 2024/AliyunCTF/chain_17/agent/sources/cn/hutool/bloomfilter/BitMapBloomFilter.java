package cn.hutool.bloomfilter;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.bloomfilter.filter.DefaultFilter;
import cn.hutool.bloomfilter.filter.ELFFilter;
import cn.hutool.bloomfilter.filter.JSFilter;
import cn.hutool.bloomfilter.filter.PJWFilter;
import cn.hutool.bloomfilter.filter.SDBMFilter;
import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/BitMapBloomFilter.class */
public class BitMapBloomFilter implements BloomFilter {
    private static final long serialVersionUID = 1;
    private BloomFilter[] filters;

    public BitMapBloomFilter(int m) {
        long mNum = NumberUtil.div(String.valueOf(m), String.valueOf(5)).longValue();
        long size = mNum * FileSize.KB_COEFFICIENT * FileSize.KB_COEFFICIENT * 8;
        this.filters = new BloomFilter[]{new DefaultFilter(size), new ELFFilter(size), new JSFilter(size), new PJWFilter(size), new SDBMFilter(size)};
    }

    public BitMapBloomFilter(int m, BloomFilter... filters) {
        this(m);
        this.filters = filters;
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean add(String str) {
        boolean flag = false;
        for (BloomFilter filter : this.filters) {
            flag |= filter.add(str);
        }
        return flag;
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean contains(String str) {
        for (BloomFilter filter : this.filters) {
            if (!filter.contains(str)) {
                return false;
            }
        }
        return true;
    }
}
