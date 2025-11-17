package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.SimpleObservation;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NoopObservation.class */
final class NoopObservation implements Observation {
    private static final Observation.Context CONTEXT = new Observation.Context();

    @Override // io.micrometer.observation.Observation
    public Observation contextualName(@Nullable String contextualName) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation parentObservation(@Nullable Observation parentObservation) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation lowCardinalityKeyValue(KeyValue keyValue) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation lowCardinalityKeyValue(String key, String value) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation highCardinalityKeyValue(KeyValue keyValue) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation highCardinalityKeyValue(String key, String value) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation observationConvention(ObservationConvention<?> observationConvention) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation error(Throwable error) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation event(Observation.Event event) {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation start() {
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation.Context getContext() {
        return CONTEXT;
    }

    @Override // io.micrometer.observation.Observation
    public void stop() {
    }

    @Override // io.micrometer.observation.Observation
    public Observation.Scope openScope() {
        return new SimpleObservation.SimpleScope(NoopObservationRegistry.FOR_SCOPES, this);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NoopObservation$NoopScope.class */
    static final class NoopScope implements Observation.Scope {
        static final Observation.Scope INSTANCE = new NoopScope();

        private NoopScope() {
        }

        @Override // io.micrometer.observation.Observation.Scope
        public Observation getCurrentObservation() {
            return Observation.NOOP;
        }

        @Override // io.micrometer.observation.Observation.Scope, java.lang.AutoCloseable
        public void close() {
        }

        @Override // io.micrometer.observation.Observation.Scope
        public void reset() {
        }

        @Override // io.micrometer.observation.Observation.Scope
        public void makeCurrent() {
        }
    }
}
