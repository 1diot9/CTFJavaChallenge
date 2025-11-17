package org.springframework.boot.context.metrics.buffering;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.springframework.core.metrics.StartupStep;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/metrics/buffering/StartupTimeline.class */
public class StartupTimeline {
    private final Instant startTime;
    private final List<TimelineEvent> events;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartupTimeline(Instant startTime, List<TimelineEvent> events) {
        this.startTime = startTime;
        this.events = Collections.unmodifiableList(events);
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public List<TimelineEvent> getEvents() {
        return this.events;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/metrics/buffering/StartupTimeline$TimelineEvent.class */
    public static class TimelineEvent {
        private final BufferedStartupStep step;
        private final Instant endTime;
        private final Duration duration;

        /* JADX INFO: Access modifiers changed from: package-private */
        public TimelineEvent(BufferedStartupStep step, Instant endTime) {
            this.step = step;
            this.endTime = endTime;
            this.duration = Duration.between(step.getStartTime(), endTime);
        }

        public Instant getStartTime() {
            return this.step.getStartTime();
        }

        public Instant getEndTime() {
            return this.endTime;
        }

        public Duration getDuration() {
            return this.duration;
        }

        public StartupStep getStartupStep() {
            return this.step;
        }
    }
}
