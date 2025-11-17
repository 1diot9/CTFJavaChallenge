package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/JdkClientHttpRequestFactory.class */
public class JdkClientHttpRequestFactory implements ClientHttpRequestFactory {
    private final HttpClient httpClient;
    private final Executor executor;

    @Nullable
    private Duration readTimeout;

    public JdkClientHttpRequestFactory() {
        this(HttpClient.newHttpClient());
    }

    public JdkClientHttpRequestFactory(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
        this.executor = (Executor) httpClient.executor().orElseGet(SimpleAsyncTaskExecutor::new);
    }

    public JdkClientHttpRequestFactory(HttpClient httpClient, Executor executor) {
        Assert.notNull(httpClient, "HttpClient is required");
        Assert.notNull(executor, "Executor must not be null");
        this.httpClient = httpClient;
        this.executor = executor;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = Duration.ofMillis(readTimeout);
    }

    public void setReadTimeout(Duration readTimeout) {
        Assert.notNull(readTimeout, "ReadTimeout must not be null");
        this.readTimeout = readTimeout;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return new JdkClientHttpRequest(this.httpClient, uri, httpMethod, this.executor, this.readTimeout);
    }
}
