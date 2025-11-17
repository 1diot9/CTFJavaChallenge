package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.Collection;
import java.util.Collections;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NoopObservationConfig.class */
public final class NoopObservationConfig extends ObservationRegistry.ObservationConfig {
    static final NoopObservationConfig INSTANCE = new NoopObservationConfig();

    private NoopObservationConfig() {
    }

    @Override // io.micrometer.observation.ObservationRegistry.ObservationConfig
    public ObservationRegistry.ObservationConfig observationHandler(ObservationHandler<?> handler) {
        return this;
    }

    @Override // io.micrometer.observation.ObservationRegistry.ObservationConfig
    public ObservationRegistry.ObservationConfig observationPredicate(ObservationPredicate predicate) {
        return this;
    }

    @Override // io.micrometer.observation.ObservationRegistry.ObservationConfig
    public boolean isObservationEnabled(String name, Observation.Context context) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // io.micrometer.observation.ObservationRegistry.ObservationConfig
    public Collection<ObservationHandler<?>> getObservationHandlers() {
        return Collections.emptyList();
    }
}
