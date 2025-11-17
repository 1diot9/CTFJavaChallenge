package org.springframework.http.server.reactive.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/observation/ServerRequestObservationConvention.class */
public interface ServerRequestObservationConvention extends ObservationConvention<ServerRequestObservationContext> {
    @Override // io.micrometer.observation.ObservationConvention
    default boolean supportsContext(Observation.Context context) {
        return context instanceof ServerRequestObservationContext;
    }
}
