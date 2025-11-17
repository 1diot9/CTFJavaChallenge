package org.springframework.scheduling;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/TriggerContext.class */
public interface TriggerContext {
    @Nullable
    Instant lastScheduledExecution();

    @Nullable
    Instant lastActualExecution();

    @Nullable
    Instant lastCompletion();

    default Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Nullable
    @Deprecated(since = "6.0")
    default Date lastScheduledExecutionTime() {
        Instant instant = lastScheduledExecution();
        if (instant != null) {
            return Date.from(instant);
        }
        return null;
    }

    @Nullable
    @Deprecated(since = "6.0")
    default Date lastActualExecutionTime() {
        Instant instant = lastActualExecution();
        if (instant != null) {
            return Date.from(instant);
        }
        return null;
    }

    @Nullable
    @Deprecated(since = "6.0")
    default Date lastCompletionTime() {
        Instant instant = lastCompletion();
        if (instant != null) {
            return Date.from(instant);
        }
        return null;
    }
}
