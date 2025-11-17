package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationView.class */
public interface ObservationView {
    Observation.ContextView getContextView();

    default ObservationRegistry getObservationRegistry() {
        return ObservationRegistry.NOOP;
    }

    @Nullable
    default Observation.Scope getEnclosingScope() {
        return Observation.Scope.NOOP;
    }
}
