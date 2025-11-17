package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/OkHttp3ClientHttpRequestFactory.class */
public class OkHttp3ClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {
    private OkHttpClient client;
    private final boolean defaultClient;

    public OkHttp3ClientHttpRequestFactory() {
        this.client = new OkHttpClient();
        this.defaultClient = true;
    }

    public OkHttp3ClientHttpRequestFactory(OkHttpClient client) {
        Assert.notNull(client, "OkHttpClient must not be null");
        this.client = client;
        this.defaultClient = false;
    }

    public void setReadTimeout(int readTimeout) {
        this.client = this.client.newBuilder().readTimeout(readTimeout, TimeUnit.MILLISECONDS).build();
    }

    public void setReadTimeout(Duration readTimeout) {
        this.client = this.client.newBuilder().readTimeout(readTimeout).build();
    }

    public void setWriteTimeout(int writeTimeout) {
        this.client = this.client.newBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS).build();
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.client = this.client.newBuilder().writeTimeout(writeTimeout).build();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.client = this.client.newBuilder().connectTimeout(connectTimeout, TimeUnit.MILLISECONDS).build();
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.client = this.client.newBuilder().connectTimeout(connectTimeout).build();
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
        return new OkHttp3ClientHttpRequest(this.client, uri, httpMethod);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws IOException {
        if (this.defaultClient) {
            Cache cache = this.client.cache();
            if (cache != null) {
                cache.close();
            }
            this.client.dispatcher().executorService().shutdown();
            this.client.connectionPool().evictAll();
        }
    }
}
