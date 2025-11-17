package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NoopObservationRegistry.class */
final class NoopObservationRegistry implements ObservationRegistry {
    static final ObservationRegistry FOR_SCOPES = ObservationRegistry.create();
    private final ObservationRegistry.ObservationConfig observationConfig = NoopObservationConfig.INSTANCE;

    @Override // io.micrometer.observation.ObservationRegistry
    public Observation getCurrentObservation() {
        return FOR_SCOPES.getCurrentObservation();
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public Observation.Scope getCurrentObservationScope() {
        return FOR_SCOPES.getCurrentObservationScope();
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public void setCurrentObservationScope(Observation.Scope current) {
        FOR_SCOPES.setCurrentObservationScope(current);
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public ObservationRegistry.ObservationConfig observationConfig() {
        return this.observationConfig;
    }
}
