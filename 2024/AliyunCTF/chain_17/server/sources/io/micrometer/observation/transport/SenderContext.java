package io.micrometer.observation.transport;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.transport.Propagator;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/transport/SenderContext.class */
public class SenderContext<C> extends Observation.Context {
    private final Propagator.Setter<C> setter;
    private final Kind kind;

    @Nullable
    private C carrier;

    @Nullable
    private String remoteServiceName;

    @Nullable
    private String remoteServiceAddress;

    public SenderContext(@NonNull Propagator.Setter<C> setter, @NonNull Kind kind) {
        this.setter = (Propagator.Setter) Objects.requireNonNull(setter, "Setter must be set");
        this.kind = (Kind) Objects.requireNonNull(kind, "Kind must be set");
    }

    public SenderContext(@NonNull Propagator.Setter<C> setter) {
        this(setter, Kind.PRODUCER);
    }

    @Nullable
    public C getCarrier() {
        return this.carrier;
    }

    public void setCarrier(C carrier) {
        this.carrier = carrier;
    }

    public Propagator.Setter<C> getSetter() {
        return this.setter;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Nullable
    public String getRemoteServiceName() {
        return this.remoteServiceName;
    }

    public void setRemoteServiceName(@Nullable String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    @Nullable
    public String getRemoteServiceAddress() {
        return this.remoteServiceAddress;
    }

    public void setRemoteServiceAddress(@Nullable String remoteServiceAddress) {
        this.remoteServiceAddress = remoteServiceAddress;
    }
}
