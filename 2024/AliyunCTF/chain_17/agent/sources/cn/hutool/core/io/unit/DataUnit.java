package cn.hutool.core.io.unit;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/unit/DataUnit.class */
public enum DataUnit {
    BYTES("B", DataSize.ofBytes(1)),
    KILOBYTES("KB", DataSize.ofKilobytes(1)),
    MEGABYTES("MB", DataSize.ofMegabytes(1)),
    GIGABYTES("GB", DataSize.ofGigabytes(1)),
    TERABYTES("TB", DataSize.ofTerabytes(1));

    public static final String[] UNIT_NAMES = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
    private final String suffix;
    private final DataSize size;

    DataUnit(String suffix, DataSize size) {
        this.suffix = suffix;
        this.size = size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataSize size() {
        return this.size;
    }

    public static DataUnit fromSuffix(String suffix) {
        for (DataUnit candidate : values()) {
            if (StrUtil.startWithIgnoreCase(candidate.suffix, suffix)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
    }
}
