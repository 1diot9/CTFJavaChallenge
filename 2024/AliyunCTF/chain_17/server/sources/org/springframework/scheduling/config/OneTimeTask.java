package org.springframework.scheduling.config;

import java.time.Duration;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/OneTimeTask.class */
public class OneTimeTask extends DelayedTask {
    public OneTimeTask(Runnable runnable, Duration initialDelay) {
        super(runnable, initialDelay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OneTimeTask(DelayedTask task) {
        super(task);
    }
}
