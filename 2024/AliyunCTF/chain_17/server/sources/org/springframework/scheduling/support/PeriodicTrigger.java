package org.springframework.scheduling.support;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/PeriodicTrigger.class */
public class PeriodicTrigger implements Trigger {
    private final Duration period;

    @Nullable
    private final ChronoUnit chronoUnit;

    @Nullable
    private volatile Duration initialDelay;
    private volatile boolean fixedRate;

    @Deprecated(since = "6.0")
    public PeriodicTrigger(long period) {
        this(period, (TimeUnit) null);
    }

    @Deprecated(since = "6.0")
    public PeriodicTrigger(long period, @Nullable TimeUnit timeUnit) {
        this(toDuration(period, timeUnit), timeUnit);
    }

    private static Duration toDuration(long amount, @Nullable TimeUnit timeUnit) {
        if (timeUnit != null) {
            return Duration.of(amount, timeUnit.toChronoUnit());
        }
        return Duration.ofMillis(amount);
    }

    public PeriodicTrigger(Duration period) {
        this(period, (TimeUnit) null);
    }

    private PeriodicTrigger(Duration period, @Nullable TimeUnit timeUnit) {
        Assert.notNull(period, "Period must not be null");
        Assert.isTrue(!period.isNegative(), "Period must not be negative");
        this.period = period;
        if (timeUnit != null) {
            this.chronoUnit = timeUnit.toChronoUnit();
        } else {
            this.chronoUnit = null;
        }
    }

    @Deprecated(since = "6.0")
    public long getPeriod() {
        if (this.chronoUnit != null) {
            return this.period.get(this.chronoUnit);
        }
        return this.period.toMillis();
    }

    public Duration getPeriodDuration() {
        return this.period;
    }

    @Deprecated(since = "6.0")
    public TimeUnit getTimeUnit() {
        if (this.chronoUnit != null) {
            return TimeUnit.of(this.chronoUnit);
        }
        return TimeUnit.MILLISECONDS;
    }

    @Deprecated(since = "6.0")
    public void setInitialDelay(long initialDelay) {
        if (this.chronoUnit != null) {
            this.initialDelay = Duration.of(initialDelay, this.chronoUnit);
        } else {
            this.initialDelay = Duration.ofMillis(initialDelay);
        }
    }

    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    @Deprecated(since = "6.0")
    public long getInitialDelay() {
        Duration initialDelay = this.initialDelay;
        if (initialDelay != null) {
            if (this.chronoUnit != null) {
                return initialDelay.get(this.chronoUnit);
            }
            return initialDelay.toMillis();
        }
        return 0L;
    }

    @Nullable
    public Duration getInitialDelayDuration() {
        return this.initialDelay;
    }

    public void setFixedRate(boolean fixedRate) {
        this.fixedRate = fixedRate;
    }

    public boolean isFixedRate() {
        return this.fixedRate;
    }

    @Override // org.springframework.scheduling.Trigger
    public Instant nextExecution(TriggerContext triggerContext) {
        Instant lastExecution = triggerContext.lastScheduledExecution();
        Instant lastCompletion = triggerContext.lastCompletion();
        if (lastExecution == null || lastCompletion == null) {
            Instant instant = triggerContext.getClock().instant();
            Duration initialDelay = this.initialDelay;
            if (initialDelay == null) {
                return instant;
            }
            return instant.plus((TemporalAmount) initialDelay);
        }
        if (this.fixedRate) {
            return lastExecution.plus((TemporalAmount) this.period);
        }
        return lastCompletion.plus((TemporalAmount) this.period);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof PeriodicTrigger) {
                PeriodicTrigger that = (PeriodicTrigger) other;
                if (this.fixedRate != that.fixedRate || !this.period.equals(that.period) || !ObjectUtils.nullSafeEquals(this.initialDelay, that.initialDelay)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.period.hashCode();
    }
}
