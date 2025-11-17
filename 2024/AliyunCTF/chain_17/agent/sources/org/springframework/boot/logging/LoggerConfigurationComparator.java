package org.springframework.boot.logging;

import java.util.Comparator;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggerConfigurationComparator.class */
class LoggerConfigurationComparator implements Comparator<LoggerConfiguration> {
    private final String rootLoggerName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoggerConfigurationComparator(String rootLoggerName) {
        Assert.notNull(rootLoggerName, "RootLoggerName must not be null");
        this.rootLoggerName = rootLoggerName;
    }

    @Override // java.util.Comparator
    public int compare(LoggerConfiguration o1, LoggerConfiguration o2) {
        if (this.rootLoggerName.equals(o1.getName())) {
            return -1;
        }
        if (this.rootLoggerName.equals(o2.getName())) {
            return 1;
        }
        return o1.getName().compareTo(o2.getName());
    }
}
