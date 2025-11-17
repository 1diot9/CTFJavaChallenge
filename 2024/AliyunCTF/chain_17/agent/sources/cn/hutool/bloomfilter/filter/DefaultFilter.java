package cn.hutool.bloomfilter.filter;

import cn.hutool.core.util.HashUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/DefaultFilter.class */
public class DefaultFilter extends FuncFilter {
    private static final long serialVersionUID = 1;

    public DefaultFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public DefaultFilter(long maxValue, int machineNumber) {
        super(maxValue, machineNumber, HashUtil::javaDefaultHash);
    }
}
