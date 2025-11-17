package io.micrometer.observation.transport;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Propagator;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/transport/RequestReplyReceiverContext.class */
public class RequestReplyReceiverContext<C, RES> extends ReceiverContext<C> implements ResponseContext<RES> {

    @Nullable
    private RES response;

    public RequestReplyReceiverContext(@NonNull Propagator.Getter<C> getter, @NonNull Kind kind) {
        super(getter, kind);
    }

    public RequestReplyReceiverContext(@NonNull Propagator.Getter<C> getter) {
        this(getter, Kind.SERVER);
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
