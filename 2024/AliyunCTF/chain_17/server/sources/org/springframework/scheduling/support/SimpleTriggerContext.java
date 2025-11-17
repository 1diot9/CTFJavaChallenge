package org.springframework.scheduling.support;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TriggerContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/SimpleTriggerContext.class */
public class SimpleTriggerContext implements TriggerContext {
    private final Clock clock;

    @Nullable
    private volatile Instant lastScheduledExecution;

    @Nullable
    private volatile Instant lastActualExecution;

    @Nullable
    private volatile Instant lastCompletion;

    public SimpleTriggerContext() {
        this.clock = Clock.systemDefaultZone();
    }

    @Deprecated(since = "6.0")
    public SimpleTriggerContext(@Nullable Date lastScheduledExecutionTime, @Nullable Date lastActualExecutionTime, @Nullable Date lastCompletionTime) {
        this(toInstant(lastScheduledExecutionTime), toInstant(lastActualExecutionTime), toInstant(lastCompletionTime));
    }

    @Nullable
    private static Instant toInstant(@Nullable Date date) {
        if (date != null) {
            return date.toInstant();
        }
        return null;
    }

    public SimpleTriggerContext(@Nullable Instant lastScheduledExecution, @Nullable Instant lastActualExecution, @Nullable Instant lastCompletion) {
        this();
        this.lastScheduledExecution = lastScheduledExecution;
        this.lastActualExecution = lastActualExecution;
        this.lastCompletion = lastCompletion;
    }

    public SimpleTriggerContext(Clock clock) {
        this.clock = clock;
    }

    @Deprecated(since = "6.0")
    public void update(@Nullable Date lastScheduledExecutionTime, @Nullable Date lastActualExecutionTime, @Nullable Date lastCompletionTime) {
        update(toInstant(lastScheduledExecutionTime), toInstant(lastActualExecutionTime), toInstant(lastCompletionTime));
    }

    public void update(@Nullable Instant lastScheduledExecution, @Nullable Instant lastActualExecution, @Nullable Instant lastCompletion) {
        this.lastScheduledExecution = lastScheduledExecution;
        this.lastActualExecution = lastActualExecution;
        this.lastCompletion = lastCompletion;
    }

    @Override // org.springframework.scheduling.TriggerContext
    public Clock getClock() {
        return this.clock;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Instant lastScheduledExecution() {
        return this.lastScheduledExecution;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Instant lastActualExecution() {
        return this.lastActualExecution;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Instant lastCompletion() {
        return this.lastCompletion;
    }
}
