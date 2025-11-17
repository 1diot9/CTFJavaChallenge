package org.springframework.scheduling.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/CronTrigger.class */
public class CronTrigger implements Trigger {
    private final CronExpression expression;

    @Nullable
    private final ZoneId zoneId;

    public CronTrigger(String expression) {
        this.expression = CronExpression.parse(expression);
        this.zoneId = null;
    }

    public CronTrigger(String expression, TimeZone timeZone) {
        this.expression = CronExpression.parse(expression);
        Assert.notNull(timeZone, "TimeZone must not be null");
        this.zoneId = timeZone.toZoneId();
    }

    public CronTrigger(String expression, ZoneId zoneId) {
        this.expression = CronExpression.parse(expression);
        Assert.notNull(zoneId, "ZoneId must not be null");
        this.zoneId = zoneId;
    }

    public String getExpression() {
        return this.expression.toString();
    }

    @Override // org.springframework.scheduling.Trigger
    public Instant nextExecution(TriggerContext triggerContext) {
        Instant timestamp = determineLatestTimestamp(triggerContext);
        ZoneId zone = this.zoneId != null ? this.zoneId : triggerContext.getClock().getZone();
        ZonedDateTime zonedTimestamp = ZonedDateTime.ofInstant(timestamp, zone);
        ZonedDateTime nextTimestamp = (ZonedDateTime) this.expression.next(zonedTimestamp);
        if (nextTimestamp != null) {
            return nextTimestamp.toInstant();
        }
        return null;
    }

    Instant determineLatestTimestamp(TriggerContext triggerContext) {
        Instant timestamp = triggerContext.lastCompletion();
        if (timestamp != null) {
            Instant scheduled = triggerContext.lastScheduledExecution();
            if (scheduled != null && timestamp.isBefore(scheduled)) {
                timestamp = scheduled;
            }
        } else {
            timestamp = determineInitialTimestamp(triggerContext);
        }
        return timestamp;
    }

    Instant determineInitialTimestamp(TriggerContext triggerContext) {
        return triggerContext.getClock().instant();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof CronTrigger) {
                CronTrigger that = (CronTrigger) other;
                if (this.expression.equals(that.expression)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.expression.hashCode();
    }

    public String toString() {
        return this.expression.toString();
    }

    public static CronTrigger forLenientExecution(String expression) {
        return new CronTrigger(expression);
    }

    public static CronTrigger resumeLenientExecution(String expression, final Instant resumptionTimestamp) {
        return new CronTrigger(expression) { // from class: org.springframework.scheduling.support.CronTrigger.1
            @Override // org.springframework.scheduling.support.CronTrigger
            Instant determineInitialTimestamp(TriggerContext triggerContext) {
                return resumptionTimestamp;
            }
        };
    }

    public static CronTrigger forFixedExecution(String expression) {
        return new CronTrigger(expression) { // from class: org.springframework.scheduling.support.CronTrigger.2
            @Override // org.springframework.scheduling.support.CronTrigger
            protected Instant determineLatestTimestamp(TriggerContext triggerContext) {
                Instant scheduled = triggerContext.lastScheduledExecution();
                return scheduled != null ? scheduled : super.determineInitialTimestamp(triggerContext);
            }
        };
    }

    public static CronTrigger resumeFixedExecution(String expression, final Instant resumptionTimestamp) {
        return new CronTrigger(expression) { // from class: org.springframework.scheduling.support.CronTrigger.3
            @Override // org.springframework.scheduling.support.CronTrigger
            protected Instant determineLatestTimestamp(TriggerContext triggerContext) {
                Instant scheduled = triggerContext.lastScheduledExecution();
                return scheduled != null ? scheduled : super.determineLatestTimestamp(triggerContext);
            }

            @Override // org.springframework.scheduling.support.CronTrigger
            Instant determineInitialTimestamp(TriggerContext triggerContext) {
                return resumptionTimestamp;
            }
        };
    }
}
