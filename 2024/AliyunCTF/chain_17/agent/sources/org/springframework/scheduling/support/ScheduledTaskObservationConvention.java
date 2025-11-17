package org.springframework.scheduling.support;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/ScheduledTaskObservationConvention.class */
public interface ScheduledTaskObservationConvention extends ObservationConvention<ScheduledTaskObservationContext> {
    @Override // io.micrometer.observation.ObservationConvention
    default boolean supportsContext(Observation.Context context) {
        return context instanceof ScheduledTaskObservationContext;
    }
}
