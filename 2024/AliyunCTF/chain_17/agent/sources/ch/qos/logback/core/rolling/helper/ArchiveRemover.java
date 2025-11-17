package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAware;
import java.time.Instant;
import java.util.concurrent.Future;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/ArchiveRemover.class */
public interface ArchiveRemover extends ContextAware {
    void clean(Instant instant);

    void setMaxHistory(int i);

    void setTotalSizeCap(long j);

    Future<?> cleanAsynchronously(Instant instant);
}
