package io.micrometer.observation;

import io.micrometer.observation.Observation;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationFilter.class */
public interface ObservationFilter {
    Observation.Context map(Observation.Context context);
}
