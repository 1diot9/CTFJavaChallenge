package io.micrometer.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationConvention.class */
public interface ObservationConvention<T extends Observation.Context> extends KeyValuesConvention {
    public static final ObservationConvention<Observation.Context> EMPTY = context -> {
        return false;
    };

    boolean supportsContext(Observation.Context context);

    default KeyValues getLowCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    default KeyValues getHighCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    @Nullable
    default String getName() {
        return null;
    }

    @Nullable
    default String getContextualName(T context) {
        return null;
    }
}
