package cn.hutool.bloomfilter.filter;

import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/FuncFilter.class */
public class FuncFilter extends AbstractFilter {
    private static final long serialVersionUID = 1;
    private final Function<String, Number> hashFunc;

    public FuncFilter(long maxValue, Function<String, Number> hashFunc) {
        this(maxValue, DEFAULT_MACHINE_NUM, hashFunc);
    }

    public FuncFilter(long maxValue, int machineNum, Function<String, Number> hashFunc) {
        super(maxValue, machineNum);
        this.hashFunc = hashFunc;
    }

    @Override // cn.hutool.bloomfilter.filter.AbstractFilter
    public long hash(String str) {
        return this.hashFunc.apply(str).longValue() % this.size;
    }
}
