package org.springframework.http.server.observation;

import io.micrometer.observation.transport.RequestReplyReceiverContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/observation/ServerRequestObservationContext.class */
public class ServerRequestObservationContext extends RequestReplyReceiverContext<HttpServletRequest, HttpServletResponse> {

    @Nullable
    private String pathPattern;

    public ServerRequestObservationContext(HttpServletRequest request, HttpServletResponse response) {
        super((v0, v1) -> {
            return v0.getHeader(v1);
        });
        setCarrier(request);
        setResponse(response);
    }

    @Nullable
    public String getPathPattern() {
        return this.pathPattern;
    }

    public void setPathPattern(@Nullable String pathPattern) {
        this.pathPattern = pathPattern;
    }
}
