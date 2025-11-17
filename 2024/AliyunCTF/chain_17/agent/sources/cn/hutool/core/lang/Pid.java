package cn.hutool.core.lang;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;
import java.lang.management.ManagementFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Pid.class */
public enum Pid {
    INSTANCE;

    private final int pid = getPid();

    Pid() {
    }

    public int get() {
        return this.pid;
    }

    private static int getPid() throws UtilException {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        if (StrUtil.isBlank(processName)) {
            throw new UtilException("Process name is blank!");
        }
        int atIndex = processName.indexOf(64);
        if (atIndex > 0) {
            return Integer.parseInt(processName.substring(0, atIndex));
        }
        return processName.hashCode();
    }
}
