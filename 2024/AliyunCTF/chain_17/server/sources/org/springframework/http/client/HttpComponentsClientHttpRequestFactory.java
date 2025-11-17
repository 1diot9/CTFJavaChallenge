package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/HttpComponentsClientHttpRequestFactory.class */
public class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {
    private HttpClient httpClient;

    @Nullable
    private BiFunction<HttpMethod, URI, HttpContext> httpContextFactory;
    private long connectTimeout;
    private long connectionRequestTimeout;

    public HttpComponentsClientHttpRequestFactory() {
        this.connectTimeout = -1L;
        this.connectionRequestTimeout = -1L;
        this.httpClient = HttpClients.createSystem();
    }

    public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        this.connectTimeout = -1L;
        this.connectionRequestTimeout = -1L;
        this.httpClient = httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int connectTimeout) {
        Assert.isTrue(connectTimeout >= 0, "Timeout must be a non-negative value");
        this.connectTimeout = connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        Assert.notNull(connectTimeout, "ConnectTimeout must not be null");
        Assert.isTrue(!connectTimeout.isNegative(), "Timeout must be a non-negative value");
        this.connectTimeout = connectTimeout.toMillis();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        Assert.isTrue(connectionRequestTimeout >= 0, "Timeout must be a non-negative value");
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Duration connectionRequestTimeout) {
        Assert.notNull(connectionRequestTimeout, "ConnectionRequestTimeout must not be null");
        Assert.isTrue(!connectionRequestTimeout.isNegative(), "Timeout must be a non-negative value");
        this.connectionRequestTimeout = connectionRequestTimeout.toMillis();
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public void setBufferRequestBody(boolean bufferRequestBody) {
    }

    public void setHttpContextFactory(BiFunction<HttpMethod, URI, HttpContext> httpContextFactory) {
        this.httpContextFactory = httpContextFactory;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpClient client = getHttpClient();
        Configurable createHttpUriRequest = createHttpUriRequest(httpMethod, uri);
        postProcessHttpRequest(createHttpUriRequest);
        HttpClientContext createHttpContext = createHttpContext(httpMethod, uri);
        if (createHttpContext == null) {
            createHttpContext = HttpClientContext.create();
        }
        if (createHttpContext.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (createHttpUriRequest instanceof Configurable) {
                Configurable configurable = createHttpUriRequest;
                config = configurable.getConfig();
            }
            if (config == null) {
                config = createRequestConfig(client);
            }
            if (config != null) {
                createHttpContext.setAttribute("http.request-config", config);
            }
        }
        return new HttpComponentsClientHttpRequest(client, createHttpUriRequest, createHttpContext);
    }

    @Nullable
    protected RequestConfig createRequestConfig(Object client) {
        if (client instanceof Configurable) {
            Configurable configurableClient = (Configurable) client;
            RequestConfig clientRequestConfig = configurableClient.getConfig();
            return mergeRequestConfig(clientRequestConfig);
        }
        return mergeRequestConfig(RequestConfig.DEFAULT);
    }

    protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
        if (this.connectTimeout == -1 && this.connectionRequestTimeout == -1) {
            return clientConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy(clientConfig);
        if (this.connectTimeout >= 0) {
            builder.setConnectTimeout(this.connectTimeout, TimeUnit.MILLISECONDS);
        }
        if (this.connectionRequestTimeout >= 0) {
            builder.setConnectionRequestTimeout(this.connectionRequestTimeout, TimeUnit.MILLISECONDS);
        }
        return builder.build();
    }

    protected ClassicHttpRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        if (HttpMethod.GET.equals(httpMethod)) {
            return new HttpGet(uri);
        }
        if (HttpMethod.HEAD.equals(httpMethod)) {
            return new HttpHead(uri);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            return new HttpPost(uri);
        }
        if (HttpMethod.PUT.equals(httpMethod)) {
            return new HttpPut(uri);
        }
        if (HttpMethod.PATCH.equals(httpMethod)) {
            return new HttpPatch(uri);
        }
        if (HttpMethod.DELETE.equals(httpMethod)) {
            return new HttpDelete(uri);
        }
        if (HttpMethod.OPTIONS.equals(httpMethod)) {
            return new HttpOptions(uri);
        }
        if (HttpMethod.TRACE.equals(httpMethod)) {
            return new HttpTrace(uri);
        }
        throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
    }

    protected void postProcessHttpRequest(ClassicHttpRequest request) {
    }

    @Nullable
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        if (this.httpContextFactory != null) {
            return this.httpContextFactory.apply(httpMethod, uri);
        }
        return null;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        Closeable httpClient = getHttpClient();
        if (httpClient instanceof Closeable) {
            Closeable closeable = httpClient;
            closeable.close();
        }
    }
}
