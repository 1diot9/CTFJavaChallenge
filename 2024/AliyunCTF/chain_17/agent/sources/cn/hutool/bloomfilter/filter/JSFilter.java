package cn.hutool.bloomfilter.filter;

import cn.hutool.core.util.HashUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/JSFilter.class */
public class JSFilter extends FuncFilter {
    private static final long serialVersionUID = 1;

    public JSFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public JSFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, HashUtil::jsHash);
    }
}
