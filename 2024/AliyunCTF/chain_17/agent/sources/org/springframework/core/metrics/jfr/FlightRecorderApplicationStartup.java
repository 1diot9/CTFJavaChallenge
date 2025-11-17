package org.springframework.core.metrics.jfr;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderApplicationStartup.class */
public class FlightRecorderApplicationStartup implements ApplicationStartup {
    private final AtomicLong currentSequenceId = new AtomicLong();
    private final Deque<Long> currentSteps = new ConcurrentLinkedDeque();

    public FlightRecorderApplicationStartup() {
        this.currentSteps.offerFirst(Long.valueOf(this.currentSequenceId.get()));
    }

    @Override // org.springframework.core.metrics.ApplicationStartup
    public StartupStep start(String name) {
        long sequenceId = this.currentSequenceId.incrementAndGet();
        this.currentSteps.offerFirst(Long.valueOf(sequenceId));
        return new FlightRecorderStartupStep(sequenceId, name, this.currentSteps.getFirst().longValue(), committedStep -> {
            this.currentSteps.removeFirstOccurrence(Long.valueOf(sequenceId));
        });
    }
}
