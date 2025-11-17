package io.micrometer.observation;

import io.micrometer.observation.Observation;
import java.util.function.BiPredicate;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationPredicate.class */
public interface ObservationPredicate extends BiPredicate<String, Observation.Context> {
}
