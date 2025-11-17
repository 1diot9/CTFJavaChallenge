package org.springframework.http.client.observation;

import io.micrometer.observation.transport.RequestReplySenderContext;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/ClientRequestObservationContext.class */
public class ClientRequestObservationContext extends RequestReplySenderContext<ClientHttpRequest, ClientHttpResponse> {

    @Nullable
    private String uriTemplate;

    public ClientRequestObservationContext(ClientHttpRequest request) {
        super(ClientRequestObservationContext::setRequestHeader);
        setCarrier(request);
    }

    private static void setRequestHeader(@Nullable ClientHttpRequest request, String name, String value) {
        if (request != null) {
            request.getHeaders().set(name, value);
        }
    }

    @Nullable
    public String getUriTemplate() {
        return this.uriTemplate;
    }

    public void setUriTemplate(@Nullable String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }
}
