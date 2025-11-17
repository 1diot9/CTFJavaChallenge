package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/GlobalObservationConvention.class */
public interface GlobalObservationConvention<T extends Observation.Context> extends ObservationConvention<T> {
}
