package cn.hutool.core.io.file;

import cn.hutool.core.text.StrPool;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/LineSeparator.class */
public enum LineSeparator {
    MAC(StrPool.CR),
    LINUX(StrPool.LF),
    WINDOWS("\r\n");

    private final String value;

    LineSeparator(String lineSeparator) {
        this.value = lineSeparator;
    }

    public String getValue() {
        return this.value;
    }
}
