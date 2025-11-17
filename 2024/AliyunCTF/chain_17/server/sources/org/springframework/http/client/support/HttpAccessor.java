package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/support/HttpAccessor.class */
public abstract class HttpAccessor {
    protected final Log logger = HttpLogging.forLogName(getClass());
    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    private final List<ClientHttpRequestInitializer> clientHttpRequestInitializers = new ArrayList();

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }

    public void setClientHttpRequestInitializers(List<ClientHttpRequestInitializer> clientHttpRequestInitializers) {
        if (this.clientHttpRequestInitializers != clientHttpRequestInitializers) {
            this.clientHttpRequestInitializers.clear();
            this.clientHttpRequestInitializers.addAll(clientHttpRequestInitializers);
            AnnotationAwareOrderComparator.sort(this.clientHttpRequestInitializers);
        }
    }

    public List<ClientHttpRequestInitializer> getClientHttpRequestInitializers() {
        return this.clientHttpRequestInitializers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = getRequestFactory().createRequest(url, method);
        initialize(request);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("HTTP " + method.name() + " " + url);
        }
        return request;
    }

    private void initialize(ClientHttpRequest request) {
        this.clientHttpRequestInitializers.forEach(initializer -> {
            initializer.initialize(request);
        });
    }
}
