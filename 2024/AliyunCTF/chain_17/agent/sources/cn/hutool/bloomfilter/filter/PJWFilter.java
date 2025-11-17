package cn.hutool.bloomfilter.filter;

import cn.hutool.core.util.HashUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/filter/PJWFilter.class */
public class PJWFilter extends FuncFilter {
    private static final long serialVersionUID = 1;

    public PJWFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public PJWFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, HashUtil::pjwHash);
    }
}
