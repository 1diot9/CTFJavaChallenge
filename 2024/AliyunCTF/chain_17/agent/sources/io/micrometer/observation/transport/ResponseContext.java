package io.micrometer.observation.transport;

import io.micrometer.common.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/transport/ResponseContext.class */
public interface ResponseContext<RES> {
    @Nullable
    RES getResponse();

    void setResponse(RES res);
}
