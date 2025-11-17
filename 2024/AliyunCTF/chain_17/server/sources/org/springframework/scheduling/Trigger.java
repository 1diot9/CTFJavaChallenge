package org.springframework.scheduling;

import java.time.Instant;
import java.util.Date;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/Trigger.class */
public interface Trigger {
    @Nullable
    Instant nextExecution(TriggerContext triggerContext);

    @Nullable
    @Deprecated(since = "6.0")
    default Date nextExecutionTime(TriggerContext triggerContext) {
        Instant instant = nextExecution(triggerContext);
        if (instant != null) {
            return Date.from(instant);
        }
        return null;
    }
}
