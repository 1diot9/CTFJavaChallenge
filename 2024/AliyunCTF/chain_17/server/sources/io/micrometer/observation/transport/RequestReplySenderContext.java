package io.micrometer.observation.transport;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Propagator;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/transport/RequestReplySenderContext.class */
public class RequestReplySenderContext<C, RES> extends SenderContext<C> implements ResponseContext<RES> {

    @Nullable
    private RES response;

    public RequestReplySenderContext(@NonNull Propagator.Setter<C> setter, @NonNull Kind kind) {
        super(setter, kind);
    }

    public RequestReplySenderContext(@NonNull Propagator.Setter<C> setter) {
        this(setter, Kind.CLIENT);
    }

    @Override // io.micrometer.observation.transport.ResponseContext
    @Nullable
    public RES getResponse() {
        return this.response;
    }

    @Override // io.micrometer.observation.transport.ResponseContext
    public void setResponse(RES response) {
        this.response = response;
    }
}
