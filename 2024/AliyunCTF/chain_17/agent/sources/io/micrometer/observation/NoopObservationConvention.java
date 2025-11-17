package io.micrometer.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.observation.Observation;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NoopObservationConvention.class */
public final class NoopObservationConvention implements ObservationConvention<Observation.Context> {
    static final NoopObservationConvention INSTANCE = new NoopObservationConvention();

    private NoopObservationConvention() {
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getName() {
        return "";
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getContextualName(Observation.Context context) {
        return "";
    }

    @Override // io.micrometer.observation.ObservationConvention
    public boolean supportsContext(Observation.Context context) {
        return ObservationConvention.EMPTY.supportsContext(context);
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getLowCardinalityKeyValues(Observation.Context context) {
        return ObservationConvention.EMPTY.getLowCardinalityKeyValues(context);
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getHighCardinalityKeyValues(Observation.Context context) {
        return ObservationConvention.EMPTY.getHighCardinalityKeyValues(context);
    }
}
