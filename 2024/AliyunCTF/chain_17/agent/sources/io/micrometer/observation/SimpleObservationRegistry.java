package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/SimpleObservationRegistry.class */
public class SimpleObservationRegistry implements ObservationRegistry {
    private static final ThreadLocal<Observation.Scope> localObservationScope = new ThreadLocal<>();
    private final ObservationRegistry.ObservationConfig observationConfig = new ObservationRegistry.ObservationConfig();

    @Override // io.micrometer.observation.ObservationRegistry
    @Nullable
    public Observation getCurrentObservation() {
        Observation.Scope scope = localObservationScope.get();
        if (scope != null) {
            return scope.getCurrentObservation();
        }
        return null;
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public Observation.Scope getCurrentObservationScope() {
        return localObservationScope.get();
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public void setCurrentObservationScope(Observation.Scope current) {
        localObservationScope.set(current);
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public ObservationRegistry.ObservationConfig observationConfig() {
        return this.observationConfig;
    }

    @Override // io.micrometer.observation.ObservationRegistry
    public boolean isNoop() {
        return super.isNoop() || observationConfig().getObservationHandlers().isEmpty();
    }
}
