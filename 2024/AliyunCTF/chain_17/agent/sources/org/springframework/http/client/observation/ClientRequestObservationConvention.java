package org.springframework.http.client.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/ClientRequestObservationConvention.class */
public interface ClientRequestObservationConvention extends ObservationConvention<ClientRequestObservationContext> {
    @Override // io.micrometer.observation.ObservationConvention
    default boolean supportsContext(Observation.Context context) {
        return context instanceof ClientRequestObservationContext;
    }
}
