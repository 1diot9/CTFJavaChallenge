package org.springframework.http.server.reactive.observation;

import io.micrometer.observation.transport.RequestReplyReceiverContext;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/observation/ServerRequestObservationContext.class */
public class ServerRequestObservationContext extends RequestReplyReceiverContext<ServerHttpRequest, ServerHttpResponse> {
    public static final String CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE = ServerRequestObservationContext.class.getName();
    private final Map<String, Object> attributes;

    @Nullable
    private String pathPattern;
    private boolean connectionAborted;

    public ServerRequestObservationContext(ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> attributes) {
        super((req, key) -> {
            return req.getHeaders().getFirst(key);
        });
        setCarrier(request);
        setResponse(response);
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Nullable
    public String getPathPattern() {
        return this.pathPattern;
    }

    public void setPathPattern(@Nullable String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public boolean isConnectionAborted() {
        return this.connectionAborted;
    }

    public void setConnectionAborted(boolean connectionAborted) {
        this.connectionAborted = connectionAborted;
    }

    public static Optional<ServerRequestObservationContext> findCurrent(Map<String, Object> attributes) {
        return Optional.ofNullable((ServerRequestObservationContext) attributes.get(CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE));
    }
}
