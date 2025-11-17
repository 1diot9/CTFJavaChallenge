package cn.hutool.bloomfilter.filter;

import cn.hutool.core.util.HashUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/ELFFilter.class */
public class ELFFilter extends FuncFilter {
    private static final long serialVersionUID = 1;

    public ELFFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public ELFFilter(long maxValue, int machineNumber) {
        super(maxValue, machineNumber, HashUtil::elfHash);
    }
}
