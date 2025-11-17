package cn.hutool.system;

import cn.hutool.core.io.FileUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/RuntimeInfo.class */
public class RuntimeInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private final Runtime currentRuntime = Runtime.getRuntime();

    public final Runtime getRuntime() {
        return this.currentRuntime;
    }

    public final long getMaxMemory() {
        return this.currentRuntime.maxMemory();
    }

    public final long getTotalMemory() {
        return this.currentRuntime.totalMemory();
    }

    public final long getFreeMemory() {
        return this.currentRuntime.freeMemory();
    }

    public final long getUsableMemory() {
        return (this.currentRuntime.maxMemory() - this.currentRuntime.totalMemory()) + this.currentRuntime.freeMemory();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        SystemUtil.append(builder, "Max Memory:    ", FileUtil.readableFileSize(getMaxMemory()));
        SystemUtil.append(builder, "Total Memory:     ", FileUtil.readableFileSize(getTotalMemory()));
        SystemUtil.append(builder, "Free Memory:     ", FileUtil.readableFileSize(getFreeMemory()));
        SystemUtil.append(builder, "Usable Memory:     ", FileUtil.readableFileSize(getUsableMemory()));
        return builder.toString();
    }
}
